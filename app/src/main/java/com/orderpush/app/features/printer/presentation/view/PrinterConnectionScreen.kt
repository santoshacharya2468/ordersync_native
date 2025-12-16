package com.orderpush.app.features.printer.presentation.view
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.printer.presentation.viewmodel.BasePrinter
import com.orderpush.app.features.printer.presentation.viewmodel.PrinterSelectionViewModel
import com.orderpush.app.features.printer.presentation.viewmodel.PrinterType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterConnectionScreen( printerType: PrinterType){
        val viewModel = hiltViewModel<PrinterSelectionViewModel>()
        val scope = rememberCoroutineScope()
        LaunchedEffect(printerType) {
            viewModel.scanForPrinters(printerType)
        }
        BaseView(title = "Available Printers") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header with scan status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${printerType.displayName} Printers",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (viewModel.isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Available Printers List
                if (viewModel.availablePrinters.isEmpty() && !viewModel.isScanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                "No printers found",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.availablePrinters) { printer ->
                            PrinterCard(
                                printer = printer,
                                isSelected = viewModel.selectedPrinter == printer,
                                isConnected = viewModel.isConnected && viewModel.selectedPrinter == printer,
                                onClick = {
                                    scope.launch {
                                        viewModel.connectToPrinter(printer)
                                    }
                                }
                            )
                        }
                    }
                }

                // Status Bar
                if (viewModel.selectedPrinter != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (viewModel.isConnected)
                                Color(0xFFC8E6C9)
                            else
                                Color(0xFFFFCDD2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (viewModel.isConnected)
                                    Icons.Default.CheckCircle
                                else
                                    Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (viewModel.isConnected)
                                    Color(0xFF2E7D32)
                                else
                                    Color(0xFFC62828)
                            )
                            Text(
                                viewModel.connectionStatus,
                                modifier = Modifier.padding(start = 12.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

}

@Composable
fun PrinterCard(
    printer: BasePrinter,
    isSelected: Boolean,
    isConnected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isConnected)
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        else
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        printer.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${printer.address}:${printer.port}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


            }
        }
    }
}
