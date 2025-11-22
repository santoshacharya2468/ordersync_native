package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun RecallOrderView(station: Station?, onClose: () -> Unit) {
    val orderViewModel =
        hiltViewModel<OrderViewModel>(key = remember { UUID.randomUUID().toString() })
    val orderState = orderViewModel.uiState.collectAsState()
    val stationViewModel=hiltViewModel<StationViewModel>()
    LaunchedEffect(Unit) {
        orderViewModel.updateFilter(orderViewModel.filterState.value.copy(
            statues =if(station?.nextStationId==null) listOf(OrderStatus.Ready) else listOf(
                OrderStatus.Ready, OrderStatus.Confirmed)
        ))
        stationViewModel.getStations()
    }
    val stationState=stationViewModel.uiState.collectAsState()
    val stations: List<Station> by remember {
        derivedStateOf {
            if (stationState.value is StationUIState.Success){
                (stationState.value as StationUIState.Success).stations
            }else{
                emptyList()

            }
        }
    }
    BaseView(
        title = "Recall orders",
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
                val orders = (orderState.value as OrderUiState.Success).orders.filter {
                   val items= if(station?.nextStationId==null)it.orderItems else it.orderItems?.filter {item->
                       val previousStation=stations.getStationByName(item.station?:"")?.id
                       station.id == previousStation && item.status== OrderItemStatus.ready
                   }
                   items?.size!=0

                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.Start,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Click on order to recall", style = MaterialTheme.typography.titleSmall)
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(orders) { order ->
                            OrderTileView(order,onClick= {
                               if(order.status!= OrderStatus.Confirmed) orderViewModel.updateOrder(order, request = UpdateOrderRequest(status = OrderStatus.Confirmed))
                                    orderViewModel.updateOrderItems(
                                        order=order,
                                        request = order.orderItems?.filter {item->
                                            val previousStation=stations.getStationByName(item.station?:"")?.id
                                            !(station?.nextStationId!=null && station.id!=previousStation)
                                        }?.map {item->
                                            UpdateOrderItemRequest(
                                                ready = false,
                                                id = item.id,
                                                status = OrderItemStatus.recall,
                                                currentStationId = stations.getStationByName(item.station?:"")?.id
                                            )
                                        }?:emptyList()
                                    )
                                onClose()
                            })
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