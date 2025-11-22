package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.orderpush.app.features.kds.data.model.KdsSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.features.kds.presentation.viewmodel.KdsViewModel


@Composable
fun KdsGeneralSettingsView(settings: KdsSettings){
    val viewModel= hiltViewModel<KdsViewModel>()
    val state=viewModel.deviceInfo.collectAsState()
    LaunchedEffect(UInt) {
        viewModel.setDeviceInfo()
    }
  if(state.value!=null)  KdsInfoCard(
        kdsName = state.value!!.name,
        currentVersion = state.value!!.version,
        ipAddress = state.value!!.ip,
        identifier = state.value!!.id,
    )
}

@Composable
fun KdsInfoCard(
    kdsName: String,
    currentVersion: String,
    ipAddress: String,
    identifier: String,
    onKdsNameClick: (() -> Unit)? = null
) {
    KdsCard(

    ) {
        InfoRow(
            label = "Device Name",
            value = kdsName,

            onClick = onKdsNameClick
        )
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        InfoRow(label = "Current Version", value = currentVersion)
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        InfoRow(label = "Ip Address", value = ipAddress)
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        InfoRow(label = "Identifier", value = identifier)
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding( vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
