package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.AppBarAction
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.core.views.LoadingPlaceHolder
import com.orderpush.app.features.kds.data.model.Station
import com.orderpush.app.features.kds.data.model.getStationByName
import com.orderpush.app.features.kds.presentation.viewmodel.StationUIState
import com.orderpush.app.features.kds.presentation.viewmodel.StationViewModel
import com.orderpush.app.features.order.data.model.OrderFilter
import com.orderpush.app.features.order.data.model.OrderItemStatus
import com.orderpush.app.features.order.data.model.OrderStatus
import com.orderpush.app.features.order.data.model.UpdateOrderItemRequest
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
import com.orderpush.app.features.order.presentation.viewmodel.OrderUiState
import com.orderpush.app.features.order.presentation.viewmodel.OrderViewModel
import com.orderpush.app.features.order.ui.components.OrderTileView
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HoldOrderView(station: Station?, onClose: () -> Unit) {
    val orderViewModel =
        hiltViewModel<OrderViewModel>(key = remember { UUID.randomUUID().toString() })
    val orderState = orderViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        orderViewModel.updateFilter(orderViewModel.filterState.value.copy(
            station =if(station?.nextStationId==null)null else station.id,
            statues = listOf(OrderStatus.Hold
        )))
    }
    BaseView(
        title = "Hold orders",
        actions = listOf(
            AppBarAction(
                icon = Icons.Default.Close,
                contentDescription = "close",
                onClick = onClose
            )
        )
    ) {
        when (orderState.value) {
            is OrderUiState.Success -> {
                val orders = (orderState.value as OrderUiState.Success).orders
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.Start,
                    modifier = Modifier.padding(16.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item{ Text("Click on order to release", style = MaterialTheme.typography.titleSmall) }
                        items(orders) { order ->
                                OrderTileView(order, onClick = {
                                    orderViewModel.updateOrder(
                                        order,
                                        request = UpdateOrderRequest(
                                            status = OrderStatus.Confirmed
                                        ))
                                },

                                    ){
                                    Text("Until:${order.holdUntil?.toLocalDateTime(TimeZone.currentSystemDefault()).toString()}")
                                }

                        }
                    }
                }

            }
            is OrderUiState.Error -> {}
            OrderUiState.Loading -> {
                LoadingPlaceHolder(
                )
            }
            OrderUiState.Idle -> {
                LoadingPlaceHolder(
                )
            }
        }
    }
}


