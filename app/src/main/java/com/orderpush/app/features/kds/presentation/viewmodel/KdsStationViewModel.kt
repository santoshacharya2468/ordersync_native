package com.orderpush.app.features.kds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.network.isSuccess
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.auth.domain.repository.AuthRepository
import com.orderpush.app.features.kds.data.model.Station
import com.orderpush.app.features.kds.domain.repository.KdsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class  StationUIState {
    object Idle : StationUIState()
    object Loading : StationUIState()
    class Success(val stations:List<Station>): StationUIState()
    class Error(val message: String) : StationUIState()
}

@HiltViewModel
class StationViewModel @Inject constructor(private val repository: KdsRepository,private val sessionManager: SessionManager,private  val authRepository: AuthRepository) : ViewModel() {

   private val _uiState = MutableStateFlow<StationUIState>(StationUIState.Idle)
    private  val _currentStation = MutableStateFlow<Station?>(null)
    val  currentStation= _currentStation.asStateFlow()
    val uiState= _uiState.asStateFlow()
    fun getStations(){
        viewModelScope.launch {
            val response= repository.getStations()
            if(response.isSuccess()){
                _uiState.value=StationUIState.Success(response.data!!)
            }else {
                _uiState.value=StationUIState.Error(response.message?:"Unknown error")
            }
        }
    }
    fun setCurrentStation(){
        viewModelScope.launch {
           val devices= authRepository.getLinkedDevices().data
            if( devices.isNullOrEmpty()){
                _currentStation.value=null
            }else{
                val device=devices.firstOrNull{
                    it.id == (sessionManager.getDeviceId() ?: "")
                }
                val response= repository.getStations()
                if(response.isSuccess()){
                    _currentStation.value=response.data?.firstOrNull{
                        it.id == (device?.stationId ?: "")
                    }
            }


            }
        }
    }

}