package com.orderpush.app.features.printer.presentation.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.order.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Collections

enum class PrinterType(val displayName: String) {
    EPSON("Epson"),
    STAR("Star Micronics"),
    X("X Printer")
}

data class BasePrinter(
    val name: String,
    val address: String,
    val port: Int = 9100,
    val type: PrinterType
)
@HiltViewModel
class PrinterSelectionViewModel @Inject constructor(private  val sessionManager: SessionManager,@ApplicationContext private  val context: Context) : ViewModel() {
    var selectedPrinterType by mutableStateOf<PrinterType?>(null)
    var availablePrinters by mutableStateOf<List<BasePrinter>>(emptyList())
    var isScanning by mutableStateOf(false)
    var selectedPrinter by mutableStateOf<BasePrinter?>(null)
    var connectionStatus by mutableStateOf("Not connected")
    var isConnected by mutableStateOf(false)
    init {
        initializeSelectedPrinter()
    }
    fun scanForPrinters(printerType: PrinterType) {
        isScanning = true
        selectedPrinterType = printerType


        viewModelScope.launch {
            try {
                val printers = Collections.synchronizedList(mutableListOf<BasePrinter>())

                // Create coroutines for all IPs and run in parallel
                val jobs = (1..254).map { i ->
                    async(Dispatchers.IO) {
                        val ip = "192.168.1.$i"
                        try {
                            if (pingPrinter(ip)) {
                                printers.add(
                                    BasePrinter(
                                        name = "${printerType.displayName} Printer",
                                        address = ip,
                                        type = printerType
                                    )
                                )
                            }
                        } catch (_: Exception) {
                            // Silently skip failed connections
                        }
                    }
                }

                // Wait for all jobs to complete
                jobs.awaitAll()

                availablePrinters = printers.toList()

                connectionStatus = if (availablePrinters.isEmpty()) {
                    "No printers found"
                } else {
                    "Found ${availablePrinters.size} printer(s)"
                }

            } catch (e: Exception) {
                connectionStatus = "Error: ${e.message}"
            } finally {
                isScanning = false
            }
        }
    }

    private suspend fun pingPrinter(address: String, port: Int = 9100): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(address, port), 500) // Reduced to 500ms
                socket.close()
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun connectToPrinter(printer: BasePrinter) {
        selectedPrinter = printer
        sessionManager.saveSelectedPrinter(selectedPrinter)
        isScanning = true
        // ✅ Launch on IO dispatcher to prevent main thread blocking
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val escPosConnection = EscPosConnection(printer.address, printer.port)

                // Connect on IO thread
                val connected = escPosConnection.connect()

                if (connected) {
                    // Update UI state back on Main thread
                    withContext(Dispatchers.Main) {
                        isConnected = true
                        connectionStatus = "Connected to ${printer.name}"
                        isScanning = false
                    }

                    // Test connection on IO thread
                    escPosConnection.testConnection()
                } else {
                    withContext(Dispatchers.Main) {
                        connectionStatus = "Failed to connect"
                        isConnected = false
                        isScanning = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    connectionStatus = "Connection error: ${e.message}"
                    isConnected = false
                    isScanning = false
                }
            }
        }
    }

    fun initializeSelectedPrinter(){
        selectedPrinter=sessionManager.getSelectedPrinter()
        if(selectedPrinter!=null){
            connectToPrinter(selectedPrinter!!)
        }
    }
    fun printReceipt(order: Order){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isConnected) {
                    val escPosConnection =
                        EscPosConnection(selectedPrinter!!.address, selectedPrinter!!.port)
                    val connected=escPosConnection.connect()
                    if(!connected){
                        withContext(Dispatchers.Main){ Toast.makeText(context, "Printer not connected", Toast.LENGTH_SHORT).show() }
                        return@launch
                    }
                   val printed= escPosConnection.printOrderReceipt(order)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, if (printed)"Order printed successfully" else "Failed to print", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main){ Toast.makeText(context, "Printer not connected", Toast.LENGTH_SHORT).show() }
                }
            }
            catch (_: Exception){}
        }

    }
}

// ESC/POS Connection Handler
class EscPosConnection(private val ipAddress: String, private val port: Int) {
    private var socket: Socket? = null
    private var outputStream: java.io.OutputStream? = null

    fun connect(): Boolean {
        return try {
            socket = Socket(ipAddress, port)
            socket?.soTimeout = 5000 // 5 second read timeout
            outputStream = socket?.getOutputStream()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun testConnection() {
        try {
            // ESC/POS: Initialize printer
            val initCommand = byteArrayOf(0x1B, 0x40) // ESC @
            outputStream?.write(initCommand)
            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun printOrderReceipt(order: Order): Boolean {
        try {
            val commands = StringBuilder()

            // Initialize
            commands.append("\u001B@")

            // Center align
            commands.append("\u001Bb\u0001")

            // Text
            commands.append("${order.storeCustomer?.name}\n\n")
            commands.append("Order Push\n")
            commands.append("================================\n\n")
            order.orderItems?.forEach{
                commands.append("${it.name}\n")
                commands.append("Qty: ${it.qty}\n")
                commands.append("Price: $${it.price}\n\n")
            }
            commands.append("================================\n")
            commands.append("Total: \$${order.total}\n\n")

            // Left align
            commands.append("\u001Bb\u0000")
            // Line feed
            commands.append("\n\n\n")
            // Cut paper
            commands.append("\u001Bm")
            outputStream?.write(commands.toString().toByteArray())
            outputStream?.flush()
            return  true

        } catch (e: Exception) {
            e.printStackTrace()
            return  false
        }
    }

    fun disconnect() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}