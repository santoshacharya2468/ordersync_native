package com.orderpush.app.features.kds.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.services.DeviceInfo
import com.orderpush.app.core.services.getDeviceInfo
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.kds.domain.repository.KdsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class KdsViewModel @Inject constructor(private val repository: KdsRepository,
    @ApplicationContext private  val context: Context
    ) : ViewModel() {
   private  val _kdsSettingsState= MutableStateFlow<KdsSettings>(KdsSettings())
    private  val _deviceInfoState= MutableStateFlow<DeviceInfo?>(null)
    val kdsSettings=_kdsSettingsState.asStateFlow()
    val deviceInfo=_deviceInfoState.asStateFlow()
    init {
        setKdsSettings()
    }
    fun setKdsSettings(){
        viewModelScope.launch {
            repository.getKdsSettings().collect {
                    if(it!=null){
                        _kdsSettingsState.value=it
                    }

            }
        }

    }

    fun saveKdsSettings(settings: KdsSettings){
        viewModelScope.launch {
            repository.saveKdsSettings(settings)
        }
    }

    fun setDeviceInfo(){
        viewModelScope.launch {
            val info= getDeviceInfo(context)
            _deviceInfoState.value=info
        }

    }

}