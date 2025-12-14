package com.orderpush.app.features.order.presentation.viewmodel
import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.R
import com.orderpush.app.core.network.ConnectivityObserver
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderFilter
import com.orderpush.app.features.order.data.model.UpdateOrderItemRequest
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
import com.orderpush.app.features.order.data.repository.SocketOrderData
import com.orderpush.app.features.order.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrderUiState {
    object Idle : OrderUiState()
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository,
    private val sessionManager: SessionManager,
    private  val networkObserver: ConnectivityObserver,
   @ApplicationContext context: Context

) : ViewModel() {
    val mMediaPlayer = MediaPlayer.create(context, R.raw.newordersound)
    init {
        fullSync()
        startPeriodicOrderSync()
        subscribeOrderEvents()
        observeNetwork()
    }

    private val _filterState = MutableStateFlow<OrderFilter>(
        OrderFilter(
            page = 1,
            limit = 100,
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<OrderUiState> =
        _filterState
            .flatMapLatest { filter ->
                repository.getOrders(filter)
            }
            .map { orders ->
                OrderUiState.Success(orders) as OrderUiState
            }
            .catch { e -> emit(OrderUiState.Error(e.message ?: "Unknown error")) }
            .stateIn(viewModelScope, SharingStarted.Lazily, OrderUiState.Idle)
    val filterState: StateFlow<OrderFilter> = _filterState
    fun subscribeOrderEvents() {
        viewModelScope.launch {
            repository.subscribeOrders().collect { orderData ->
                when (orderData) {
                    is SocketOrderData.OrderCreated -> {
                        mMediaPlayer.start()
                        repository.saveOrder(orderData.order)
                    }

                    is SocketOrderData.OrderUpdated -> {
                        repository.saveOrder(orderData.order)
                    }
                }
            }
        }
    }
    fun updateOrderItems(order: Order, request: List<UpdateOrderItemRequest>) {

        viewModelScope.launch {
            repository.markOrderItemReady(order, request)
        }
    }
    fun updateOrder(order: Order, request: UpdateOrderRequest) {
        viewModelScope.launch {
            repository.updateOrder(order, request)
        }
    }
    fun fullSync(){
        viewModelScope.launch {
            val filter=OrderFilter()
            try {
                repository.syncOrders(filter)
            }
            catch (_:Exception){

            }

        }
    }
    fun deltaSync(){
        val filter = OrderFilter()
        val lastSync = sessionManager.getLastSyncDate()
        if (!lastSync.isNullOrEmpty()) {
            filter.lastSync = lastSync
        }
            viewModelScope.launch {
                try {
                    repository.syncOrders(filter)
                }
                catch (_: Exception){}
            }
    }
    private fun startPeriodicOrderSync() {
        viewModelScope.launch {
            while (true) {
                delay(2 * 60 * 1000)
                deltaSync()


            }
        }
    }
    fun updateFilter(filter: OrderFilter) {
        _filterState.value = filter
    }

    @OptIn(FlowPreview::class)
    private fun observeNetwork() {
        viewModelScope.launch {
            networkObserver.isConnected.debounce(500).collect { isConnected ->
                if (isConnected) {
                    // Trigger a sync when network is restored
                    deltaSync()
                }
            }
        }
    }
}
