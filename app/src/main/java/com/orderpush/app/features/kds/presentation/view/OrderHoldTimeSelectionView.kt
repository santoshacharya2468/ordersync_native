package com.orderpush.app.features.kds.presentation.view
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderHoldTimeSelectionView(
    onDismiss: () -> Unit,
    onSubmit: (Instant) -> Unit
) {
    val context = LocalContext.current
    val nowInstant = Clock.System.now()
    val now = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    val selectedMinutes = remember { mutableStateOf<Int?>(null) }
    val customTime = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val quickOptions = listOf(1,5,10, 20, 30, 40, 60)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hold Order", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select hold duration:")

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickOptions.forEach { minutes ->
                        val selected = selectedMinutes.value == minutes
                        AssistChip(
                            onClick = {
                                selectedMinutes.value = minutes
                                customTime.value = ""
                            },
                            label = { Text("$minutes min") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = customTime.value,
                    onValueChange = {
                        customTime.value = it
                        selectedMinutes.value = null
                    },
                    label = { Text("Custom time (HH:mm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val holdInstant: Instant? = when {
                    // ✅ Handle quick options
                    selectedMinutes.value != null -> {
                        val holdUntil = nowInstant.plus(selectedMinutes.value!!.minutes)
                        holdUntil
                    }

                    // ✅ Handle custom time
                    customTime.value.isNotBlank() -> {
                        try {
                            val parts = customTime.value.split(":")
                            val hour = parts[0].toInt()
                            val minute = parts.getOrNull(1)?.toInt() ?: 0

                            // Build LocalDateTime for today with selected time
                            val holdUntilLdt = LocalDateTime(
                                year = now.year,
                                monthNumber = now.monthNumber,
                                dayOfMonth = now.dayOfMonth,
                                hour = hour,
                                minute = minute
                            )
                            holdUntilLdt.toInstant(TimeZone.currentSystemDefault())
                        } catch (e: Exception) {
                            Toast.makeText(context, "Invalid time format", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    }

                    else -> {
                        Toast.makeText(context, "Select or enter a hold time", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }

                holdInstant?.let {
                    onSubmit(it)
                    onDismiss()
                }
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

