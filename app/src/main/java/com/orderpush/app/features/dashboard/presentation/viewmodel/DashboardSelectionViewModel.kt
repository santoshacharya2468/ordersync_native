package com.orderpush.app.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.dashboard.presentation.view.DashboardType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class DashboardSelectionViewModel  @Inject constructor(private  val sessionManager: SessionManager): ViewModel() {

    private  var _selected= MutableStateFlow<DashboardType?>(null)
    val selected=_selected.asStateFlow()
    fun setDashboard(type: DashboardType){
        _selected.value=type
        sessionManager.switchDashboard(type)

    }
}