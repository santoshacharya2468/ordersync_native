package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.orderpush.app.features.kds.data.model.KdsSettings

@Composable
fun KdsOrderSettingsView(settings: KdsSettings){
    Column {
        Text("order settings")
    }
}