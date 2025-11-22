package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.kds.presentation.viewmodel.KdsViewModel

@Composable

fun KdsSoundSettingsView(settings: KdsSettings){
    val viewModel= hiltViewModel<KdsViewModel>()
    KdsCard {


        SettingSwitchItem(
            title="Enable new order notification sound",
            checked = settings.newOrderNotification,
            onCheckedChange = {
                viewModel.saveKdsSettings(settings.copy(
                    newOrderNotification = it
                ))
            }

        )

        HorizontalDivider()


        SettingSliderItem(
            value = settings.soundVolume.toFloat(),
            title = "Sound volume",
            onValueChanged = {
                viewModel.saveKdsSettings(settings.copy(
                    soundVolume = it.toInt()
                ))
            }
        )

    }
}