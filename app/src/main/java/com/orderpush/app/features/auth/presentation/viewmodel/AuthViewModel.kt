package com.orderpush.app.features.auth.presentation.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.network.isSuccess
import com.orderpush.app.core.session.SessionEventBus
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.auth.data.model.LinkedDevice
import com.orderpush.app.features.auth.data.model.UpdateDeviceRequest
import com.orderpush.app.features.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class AuthViewModel @Inject constructor  (private val repository: AuthRepository, private  val sessionManager: SessionManager,
  @ApplicationContext  private  val context: Context,
                                          val eventBus: SessionEventBus
    ) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState
    private  val _linkDevice= MutableStateFlow<LinkedDevice?>(null)
    val linkDevice: StateFlow<LinkedDevice?> = _linkDevice.asStateFlow()
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val response = repository.login(email, password)
                if (response.isSuccess() ){
                    val device=response.data!!.device
                    sessionManager.saveAccessToken(device.accessJti?:"")
                    sessionManager.saveRefreshToken(device.refreshTokenHash?:"")
                    sessionManager.saveStore(device.storeId?:"")
                    _linkDevice.value=device
                    sessionManager.setDeviceId(device.id)
                    Toast.makeText(context, "Device linked successfully", Toast.LENGTH_SHORT).show()
                    _loginState.value = AuthState.Success(response.data.user)

                } else {
                    _loginState.value = AuthState.Error(response.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(e.message ?: "Unknown error")
            }
       }
    }
    fun logout(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response= repository.logoutDevice()
                if(response.isSuccess()){
                    sessionManager.clearSession()
                    _loginState.value = AuthState.LogoutSuccess
                    _linkDevice.value = null
                    eventBus.emitLogout()
                }else{
                    _loginState.value = AuthState.Error(response.message?:"something went wrong")
                }

            } catch (e: Exception) {
                sessionManager.clearSession()
                _loginState.value = AuthState.LogoutSuccess
                _linkDevice.value = null
                eventBus.emitLogout()
            }
        }
    }

    fun setDevice(){
        viewModelScope.launch {
            val response=repository.getLinkedDevices()
            if(response.isSuccess()){
              _linkDevice.value  =response.data?.firstOrNull { it.id==sessionManager.getDeviceId() }

            }
        }
    }

    fun updateDevice(payload: UpdateDeviceRequest) {
        viewModelScope.launch {
            val response = repository.updateDevice(payload)
            if(response.isSuccess()){
                _linkDevice.value= response.data
            }

        }
    }
}