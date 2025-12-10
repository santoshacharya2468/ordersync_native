package com.orderpush.app.features.kds.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.viewmodel.NetworkViewModel
import com.orderpush.app.core.views.SidebarNetworkStatus
import com.orderpush.app.features.kds.data.model.OrderDisplayMode
import com.orderpush.app.features.kds.presentation.viewmodel.KdsViewModel
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderItemStatus
import com.orderpush.app.features.order.data.model.OrderStatus
import com.orderpush.app.features.order.data.model.UpdateOrderItemRequest
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
import com.orderpush.app.features.order.presentation.viewmodel.OrderUiState
import com.orderpush.app.features.order.presentation.viewmodel.OrderViewModel
import com.orderpush.app.features.printer.presentation.viewmodel.PrinterSelectionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun KdsDashboardScreen() {
    val kdsSetting = hiltViewModel<KdsViewModel>().kdsSettings.collectAsState()
    var showRecallDialog by remember { mutableStateOf(false) }
    var showHoldOrderDialog by remember { mutableStateOf(false) }
    val orderViewModel: OrderViewModel = hiltViewModel()
    val orderState by orderViewModel.uiState.collectAsState()
    val navigator = LocalNavigation.current
    val filter = orderViewModel.filterState.collectAsState()
    val networkViewModel = hiltViewModel<NetworkViewModel>()
    val connected = networkViewModel.isConnected.collectAsState()
    val printerViewModel = hiltViewModel<PrinterSelectionViewModel>()
    val holdOrderCount by remember {
        derivedStateOf { if (orderState is OrderUiState.Success) (orderState as OrderUiState.Success).orders.filter { it.status == OrderStatus.Hold }.size else 0 }
    }
    val activeOrders by remember {
        derivedStateOf { if (orderState is OrderUiState.Success) (orderState as OrderUiState.Success).orders.filter { it.status == OrderStatus.Confirmed } else emptyList() }
    }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    LaunchedEffect(kdsSetting.value.station) {
        orderViewModel.updateFilter(
            filter.value.copy(
                station = if (kdsSetting.value.station?.nextStationId == null) null else kdsSetting.value.station?.id,
                statues = listOf(
                    OrderStatus.Confirmed,
                    OrderStatus.Hold
                )
            )
        )
    }
    LaunchedEffect(connected.value) {
        if (connected.value) {
            delay(1000)
            orderViewModel.deltaSync()
        }
    }
    Box {
        Scaffold {
            AnimatedVisibility(visible = showRecallDialog) {
                BasicAlertDialog(
                    onDismissRequest = {
                        showRecallDialog = false
                    },
                    modifier = Modifier.padding(16.dp),
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false
                    )
                ) {
                    RecallOrderView(
                        station = kdsSetting.value.station,
                        onClose = {
                            showRecallDialog = false
                        })
                }
            }
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                        .padding(top = 100.dp)
                        .verticalScroll(state = rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SidebarNetworkStatus(
                        isConnected = connected.value
                    )
                    KdsActionButton(
                        text = "OPEN",
                        icon = {
                            Text(
                                "${activeOrders.size}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        },
                        onClick = {}
                    )

                    KdsActionButton(
                        text = "Recall",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Recall"
                            )
                        }, onClick = {
                            showRecallDialog = !showRecallDialog
                        }
                    )
                    KdsActionButton(
                        text = "Hold",
                        counterValue = holdOrderCount,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Hold orders"
                            )
                        }, onClick = {
                            showHoldOrderDialog = !showHoldOrderDialog
                        }
                    )
                    KdsActionButton(
                        text = "Sync",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync orders"
                            )
                        }, onClick = {
                            orderViewModel.fullSync()
                        }
                    )
                    KdsActionButton(
                        text = "Analytics",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "Analytics"
                            )
                        }, onClick = {
                            navigator.push(Screen.Analytics)
                        }
                    )
                    KdsActionButton(
                        onClick = {
                            navigator.push(Screen.KdsSettings)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "kds settings"
                            )
                        },
                        text = "Settings"

                    )

                }
                Spacer(modifier = Modifier.width(10.dp))
                if (orderState is OrderUiState.Success)
                    FlowColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(rememberScrollState())
                            .padding(it),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachColumn = if (kdsSetting.value.displayMode == OrderDisplayMode.Tiled) 5 else 1,
                    ) {
                        activeOrders.forEach { order ->
                            KdsOrderTileView(
                                order,
                                settings = kdsSetting.value,
                                onSelected = {
                                    selectedOrder = order
                                },
                                onComplete = {
                                    if (kdsSetting.value.station?.nextStationId == null) {
                                        //mark entire order ready since no station after this
                                        orderViewModel.updateOrder(
                                            order, request = UpdateOrderRequest(
                                                status = OrderStatus.Ready
                                            )
                                        )
                                    } else {
                                        orderViewModel.updateOrderItems(
                                            order,
                                            request = order.orderItems?.map { item ->
                                                UpdateOrderItemRequest(
                                                    ready = true,
                                                    id = item.id,
                                                    currentStationId = kdsSetting.value.station?.nextStationId,
                                                    status = OrderItemStatus.ready
                                                )
                                            } ?: emptyList()
                                        )

                                    }
                                },

                                onItemReady = {
                                    orderViewModel.updateOrderItems(
                                        order,
                                        request = listOf(
                                            UpdateOrderItemRequest(
                                                id = it.id,
                                                ready = it.ready != true
                                            )
                                        )
                                    )
                                })
                        }
                    }
            }
        }
        AnimatedVisibility(
            visible = selectedOrder != null,
            Modifier
                .align(Alignment.CenterEnd)
        ) {
        ///needed as animatedVisibility animates for few ms
       if(selectedOrder!=null)   KdsOrderActionDrawerView(order = selectedOrder!!,
               onUpdate = {payload->
                   orderViewModel.updateOrder(selectedOrder!!, request = payload)
                   selectedOrder=null
               },
              onPrint = {
                  printerViewModel.printReceipt(selectedOrder!!)
                  selectedOrder=null


              },
              onPriority = {
                  orderViewModel.updateOrder(
                      selectedOrder!!,
                      request = UpdateOrderRequest(
                          priority = if (selectedOrder!!.priorityAt == null) 2 else 0
                      )
                  )
                  selectedOrder=null

              },
              onClose = {
              selectedOrder=null

          })
        }
        AnimatedVisibility(
            visible = showHoldOrderDialog,
            modifier = Modifier.padding(16.dp)
        ) {
            BasicAlertDialog(
                onDismissRequest = {
                    showHoldOrderDialog = false
                }, properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                )
            ) {
                HoldOrderView(station = kdsSetting.value.station, onClose = {
                    showHoldOrderDialog = false
                })
            }
        }
    }
}
