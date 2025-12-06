package com.orderpush.app.core.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.network.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


@HiltViewModel
class NetworkViewModel @Inject constructor(
private  val   connectivityObserver: ConnectivityObserver
) : ViewModel() {
    val isConnected=connectivityObserver.isConnected
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(50000L),false)

}
