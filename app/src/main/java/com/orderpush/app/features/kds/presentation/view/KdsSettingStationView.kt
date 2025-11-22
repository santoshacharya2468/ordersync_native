package com.orderpush.app.features.kds.presentation.view
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.AppDropDownFormField
import com.orderpush.app.features.auth.data.model.UpdateDeviceRequest
import com.orderpush.app.features.auth.presentation.viewmodel.AuthViewModel
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.kds.presentation.viewmodel.KdsViewModel
import com.orderpush.app.features.kds.presentation.viewmodel.StationUIState
import com.orderpush.app.features.kds.presentation.viewmodel.StationViewModel
@Composable
fun KdsSettingStationView(kdsSettings: KdsSettings){
    val viewModel= hiltViewModel<StationViewModel>()
    val authViewModel=hiltViewModel<AuthViewModel>()
    val kdsViewModel=hiltViewModel<KdsViewModel>()
    LaunchedEffect(Unit) {
        viewModel.getStations()
        authViewModel.setDevice()
    }
    val state=viewModel.uiState.collectAsState()
    Column {
        when(state.value){
            StationUIState.Idle -> {}
            StationUIState.Loading -> {}
            is StationUIState.Success->{
                val stations=(state.value as StationUIState.Success).stations
                AppDropDownFormField(
                    items = stations,
                    label = "Station",
                    itemLabel = {it.name},
                    selectedItem =kdsSettings.station ,
                    onItemSelected = {
                        authViewModel.updateDevice(UpdateDeviceRequest(
                            stationId = it.id
                        ))
                        kdsViewModel.saveKdsSettings(kdsSettings.copy(
                            station = it
                        ))
                    }
                )
            }
            is StationUIState.Error ->{}
        }
    }
}
