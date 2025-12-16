package com.orderpush.app.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.network.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


@HiltViewModel
class NetworkViewModel @Inject constructor(
private  val   connectivityObserver: ConnectivityObserver
) : ViewModel() {
    val isConnected=connectivityObserver.isConnected
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(50000L),false)

}
