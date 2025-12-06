package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.kds.presentation.viewmodel.KdsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun KdsSoundSettingsScreen(){
    val viewModel= hiltViewModel<KdsViewModel>()
    val settings=viewModel.kdsSettings.collectAsState().value
    BaseView(
        title = "Notifications"
    ) {
        KdsCard {

            SettingSwitchItem(
                title = "Enable new order notification sound",
                checked = settings.newOrderNotification,
                onCheckedChange = {
                    viewModel.saveKdsSettings(
                        settings.copy(
                            newOrderNotification = it
                        )
                    )
                }

            )

            HorizontalDivider()


            SettingSliderItem(
                value = settings.soundVolume.toFloat(),
                title = "Sound volume",
                onValueChanged = {
                    viewModel.saveKdsSettings(
                        settings.copy(
                            soundVolume = it.toInt()
                        )
                    )
                }
            )

        }
    }
}