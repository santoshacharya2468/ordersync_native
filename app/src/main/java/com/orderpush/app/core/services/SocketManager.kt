package com.orderpush.app.core.services

import android.util.Log
import com.orderpush.app.core.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor(
    private val sessionManager: SessionManager
) {

    private var socket: Socket? = null
    private var baseUrl: String? = null
    private var isInitialized = false

    // Use a proper scope instead of creating new ones
    private val socketScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _orderEvents = MutableSharedFlow<SocketOrderEvent>()
    val orderEvents: SharedFlow<SocketOrderEvent> = _orderEvents.asSharedFlow()

    fun init(baseUrl: String) {
        // Prevent re-initialization
        if (isInitialized) {
            Log.d("SocketIO", "Socket already initialized")
            return
        }

        this.baseUrl = baseUrl

        try {
            val token = sessionManager.getAccessToken()
            val opts = IO.Options().apply {
                timeout = 5000
                reconnectionAttempts = Int.MAX_VALUE
                reconnectionDelay = 500
                reconnectionDelayMax = 3000

                extraHeaders = mapOf(
                    "Authorization" to listOf(token ?: "")
                )
            }

            socket = IO.socket(baseUrl, opts)
            setupSocketListeners()
            connect()
            isInitialized = true

        } catch (e: Exception) {
            Log.e("SocketIO", "Failed to initialize socket", e)
        }
    }

    private fun setupSocketListeners() {
        socket?.apply {
            on(Socket.EVENT_CONNECT) {
                Log.d("SocketIO", "Connected ✅")
                socketScope.launch {
                    delay(1000) // Reduced delay, 2s may be too long
                    joinStore()
                }
            }

            on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketIO", "Disconnected ❌")
            }

            on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e("SocketIO", "Connect Error: ${args.firstOrNull()}")
            }

            on("order_created") { args ->
                handleOrderEvent(args, "order_created", SocketOrderEvent::OrderCreated)
            }

            on("order_updated") { args ->
                handleOrderEvent(args, "order_updated", SocketOrderEvent::OrderUpdated)
            }
        }
    }

    private fun handleOrderEvent(
        args: Array<Any>,
        eventName: String,
        eventCreator: (Any) -> SocketOrderEvent
    ) {
        try {
            if (args.isNotEmpty()) {
                Log.d("SocketIO", "$eventName: ${args[0]}")
                socketScope.launch {
                    _orderEvents.emit(eventCreator(args[0]))
                }
            }
        } catch (e: Exception) {
            Log.e("SocketIO", "Error parsing $eventName", e)
        }
    }

    fun connect() {
        if (socket?.connected() == false) {
            try {
                socket?.connect()
                Log.d("SocketIO", "Connecting...")
            } catch (e: Exception) {
                Log.e("SocketIO", "Failed to connect", e)
            }
        }
    }

    private fun joinStore() {
        val storeName = sessionManager.getStore() ?: ""
        if (storeName.isEmpty()) {
            Log.w("SocketIO", "Store name is empty, skipping join_store")
            return
        }

        try {
            val data = JSONObject().apply {
                put("name", storeName)
            }
            socket?.emit("join_store", data)
            Log.d("SocketIO", "Emitted join_store event")
        } catch (e: Exception) {
            Log.e("SocketIO", "Failed to join store", e)
        }
    }

    fun disconnect() {
        try {
            socket?.disconnect()
            socket = null
            isInitialized = false
            Log.d("SocketIO", "Disconnected")
        } catch (e: Exception) {
            Log.e("SocketIO", "Error during disconnect", e)
        }
    }

    fun emit(event: String, data: Any) {
        try {
            if (socket?.connected() == true) {
                socket?.emit(event, data)
            } else {
                Log.w("SocketIO", "Socket not connected, cannot emit $event")
            }
        } catch (e: Exception) {
            Log.e("SocketIO", "Error emitting $event", e)
        }
    }

    fun getSocket(): Socket? = socket

    fun isConnected(): Boolean = socket?.connected() == true

    // Cleanup when the manager is destroyed
    fun cleanup() {
        disconnect()
        socketScope.cancel()
    }
}

// Sealed class for type-safe event handling
sealed class SocketOrderEvent {
    data class OrderCreated(val data: Any) : SocketOrderEvent()
    data class OrderUpdated(val data: Any) : SocketOrderEvent()
}

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {
    @Provides
    @Singleton
    fun provideSocketManager(sessionManager: SessionManager): SocketManager =
        SocketManager(sessionManager)
}