package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.features.kds.data.model.KdsFontSize
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.kds.presentation.viewmodel.KdsViewModel
import androidx.core.graphics.toColorInt
import com.orderpush.app.core.views.BaseView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KdsFontAndColorSettingsScreen(
    viewModel: KdsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val settings=viewModel.kdsSettings.collectAsState().value
    BaseView(
        title = "Fonts and colors"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {

            // -------- FONT SIZE SECTION --------
            Text(
                text = "FONT SIZE",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            FontSizeSelectionSection(
                selected = settings.fontSize,
                onSelected = { selected ->
                    viewModel.saveKdsSettings(
                        settings = settings.copy(
                            fontSize = selected
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // -------- STATUS COLORS SECTION --------
            Text(
                text = "STATUS COLORS",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ColorRow("On Time", settings.onTimeOrderColor) {
                viewModel.saveKdsSettings(
                    settings.copy(
                        onTimeOrderColor = it
                    )
                )
            }
            ColorRow("Warning", settings.warningOrderColor) {
                viewModel.saveKdsSettings(
                    settings.copy(
                        warningOrderColor = it
                    )
                )
            }
            ColorRow("Late", settings.lateOrderColor) {
                viewModel.saveKdsSettings(
                    settings.copy(
                        lateOrderColor = it
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -------- ORDER TYPE COLORS SECTION --------
            //        Text(
            //            text = "ORDER TYPE",
            //            style = MaterialTheme.typography.labelMedium,
            //            color = Color.Gray,
            //            modifier = Modifier.padding(vertical = 8.dp)
            //        )
            //
            //        ColorRow("For Here", settings.forHereColor) { viewModel.openColorPicker("forHereColor") }
            //        ColorRow("Drive Thru", settings.driveThruColor) { viewModel.openColorPicker("driveThruColor") }
            //        ColorRow("CurbSide", settings.curbsideColor) { viewModel.openColorPicker("curbsideColor") }
            //        ColorRow("To Go", settings.toGoColor) { viewModel.openColorPicker("toGoColor") }
            //        ColorRow("Delivery", settings.deliveryColor) { viewModel.openColorPicker("deliveryColor") }
            //        ColorRow("Pickup", settings.pickupColor) { viewModel.openColorPicker("pickupColor") }

            Spacer(modifier = Modifier.height(24.dp))

            // -------- TEXT COLORS --------
            Text(
                text = "ORDER PART TEXT COLOR",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ColorRow("Main Item", settings.mainTextColor) {
                viewModel.saveKdsSettings(
                    settings.copy(
                        mainTextColor = it
                    )
                )
            }
            ColorRow("Modifier", settings.modifierTextColor) {
                viewModel.saveKdsSettings(
                    settings.copy(
                        modifierTextColor = it
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

@Composable
fun FontSizeSelectionSection(selected: KdsFontSize, onSelected: (KdsFontSize) -> Unit) {
    val options = KdsFontSize.values()
    Column {
        options.forEach { font ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(font) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == font,
                    onClick = { onSelected(font) }
                )
                Text(
                    text = font.name.replace("_", " "),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ColorRow(title: String, colorHex: String, onColorSelected: ( newColor: String) -> Unit) {
    val color = remember(colorHex) { Color(colorHex.toColorInt()) }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        ColorPickerDialog(
            initialColor = color,
            onColorSelected = {
                onColorSelected(it)
                showDialog=false
            },
            onDismiss = {
                showDialog = false
            })
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDialog = true
            }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(color, CircleShape)
        )
    }
}

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    var red by remember { mutableStateOf((initialColor.red * 255).toInt()) }
    var green by remember { mutableStateOf((initialColor.green * 255).toInt()) }
    var blue by remember { mutableStateOf((initialColor.blue * 255).toInt()) }

    val selectedColor = Color(red, green, blue)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val hex = String.format("#%02X%02X%02X", red, green, blue)
                onColorSelected(hex)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Pick a Color") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(selectedColor, CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))
                ColorSlider("R", red) { red = it }
                ColorSlider("G", green) { green = it }
                ColorSlider("B", blue) { blue = it }
            }
        }
    )
}

@Composable
fun ColorSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text("$label: $value", style = MaterialTheme.typography.bodySmall)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..255f
        )
    }
}