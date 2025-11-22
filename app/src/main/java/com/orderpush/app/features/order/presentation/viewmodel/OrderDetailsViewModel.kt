package com.orderpush.app.features.order.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
sealed class OrderDetailsUiState {
    object Idle : OrderDetailsUiState()
    object Loading : OrderDetailsUiState()
    data class Success(val order: Order) : OrderDetailsUiState()
    data class Error(val message: String) : OrderDetailsUiState()
}
@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<OrderDetailsUiState>(OrderDetailsUiState.Idle)
    val uiState: StateFlow<OrderDetailsUiState> = _uiState
    fun getOrderDetails(id:String) {
        viewModelScope.launch {
            _uiState.value = OrderDetailsUiState.Loading
            try {
                val response = repository.getOrderDetails(id)
                if (response.status == 200) {
                    _uiState.value = OrderDetailsUiState.Success(response.data!!)
                } else {
                    _uiState.value = OrderDetailsUiState.Error(response.message ?: "Unknown error" )
                }
            } catch (e: Exception) {
                _uiState.value = OrderDetailsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}