package com.orderpush.app.features.store.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.features.order.presentation.viewmodel.OrderUiState
import com.orderpush.app.features.store.data.model.Store
import com.orderpush.app.features.store.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
sealed class  StoreSelectionState{
    object Idle : StoreSelectionState()
    object Loading : StoreSelectionState()
    data class Success(val stores: List<Store>) : StoreSelectionState()
    data class Error(val message: String) : StoreSelectionState()

}

@HiltViewModel
class StoreSelectionViewModel @Inject constructor(private val storeRepository: StoreRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<StoreSelectionState>(StoreSelectionState.Idle)
    val uiState: StateFlow<StoreSelectionState> = _uiState
    fun getLinkedStores(){
        viewModelScope.launch {
            _uiState.value = StoreSelectionState.Loading
            try {
                val response = storeRepository.getLinkedStores()
                if(response.status==200){
                    _uiState.value = StoreSelectionState.Success(response.data ?: emptyList())
                }
                else{
                    _uiState.value = StoreSelectionState.Error(response.message ?: "Unknown error")
                }

            }
            catch (e: Exception){
                _uiState.value = StoreSelectionState.Error(e.message ?: "Unknown error")
            }

        }
    }
}