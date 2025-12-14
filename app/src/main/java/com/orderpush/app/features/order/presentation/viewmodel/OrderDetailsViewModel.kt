package com.orderpush.app.features.order.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val selectedOrderId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<OrderDetailsUiState> =
        selectedOrderId
            .flatMapLatest { id ->
                if (id == null) {
                    flowOf(OrderDetailsUiState.Idle)
                } else {
                    repository.getOrderDetailsFlow(id)
                        .map { order ->
                            if (order == null) {
                                OrderDetailsUiState.Loading
                            } else {
                                OrderDetailsUiState.Success(order)
                            }
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = OrderDetailsUiState.Idle
            )

    fun selectOrder(id: String) {
        selectedOrderId.value = id
        refreshOrder(id)
    }

    private fun refreshOrder(id: String) {
        viewModelScope.launch {
            try {
                val response = repository.getOrderDetails(id)
                if (response.status == 200 && response.data != null) {
                    repository.saveOrder(response.data)
                }
            } catch (_: Exception) {}
        }
    }
}

