package com.orderpush.app.features.menuItem.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.network.isSuccess
import com.orderpush.app.features.menuItem.data.model.request.MenuItemFilter
import com.orderpush.app.features.menuItem.data.model.response.MenuItem
import com.orderpush.app.features.menuItem.domain.repository.MenuItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class  MenuItemUIState {
    object Loading : MenuItemUIState()
    object  Idle : MenuItemUIState()
    data class Error(val message:String): MenuItemUIState()
    data class  Success(val data:List<MenuItem>):MenuItemUIState()
}
@HiltViewModel
class MenuItemViewModel @Inject constructor(private  val menuItemRepository: MenuItemRepository) :
    ViewModel() {
        private  var _uiState = MutableStateFlow<MenuItemUIState>(MenuItemUIState.Idle)
        val uiState get() = _uiState.asStateFlow()

    fun getMenuItems(){
        viewModelScope.launch {
            _uiState.value = MenuItemUIState.Loading
            val response=menuItemRepository.getMenuItems(MenuItemFilter())
            if(response.isSuccess()){
                _uiState.value = MenuItemUIState.Success(response.data!!)

            }else{
                _uiState.value = MenuItemUIState.Error(response.message!!)
            }

        }
    }
}