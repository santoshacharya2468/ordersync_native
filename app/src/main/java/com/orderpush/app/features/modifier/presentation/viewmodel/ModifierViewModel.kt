package com.orderpush.app.features.modifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.network.isSuccess
import com.orderpush.app.features.modifier.data.model.request.ModifierFilter
import com.orderpush.app.features.modifier.data.model.response.MenuModifier
import com.orderpush.app.features.modifier.domain.repository.ModifierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class  ModifierUIState{
    object Idle: ModifierUIState()
    object Loading: ModifierUIState()
    data class Success(val data: List<MenuModifier>): ModifierUIState()
}

@HiltViewModel
class ModifierViewModel @Inject constructor(private  val repository: ModifierRepository): ViewModel() {
    var _uiState= MutableStateFlow<ModifierUIState>(ModifierUIState.Idle)
    val uiState=_uiState.asStateFlow()

    fun getModifiers(){
        _uiState.value=ModifierUIState.Loading
        viewModelScope.launch {
            val response=repository.getModifiers(ModifierFilter())
            if(response.isSuccess()){
                _uiState.value=ModifierUIState.Success(response.data!!)
            }else{
                _uiState.value=ModifierUIState.Idle
            }
        }
    }
}