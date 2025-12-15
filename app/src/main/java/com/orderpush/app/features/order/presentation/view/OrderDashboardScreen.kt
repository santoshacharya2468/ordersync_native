package com.orderpush.app.features.order.presentation.view
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.extension.filterByTab
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.dashboard.data.model.OrderFilterTabMenu
import com.orderpush.app.features.dashboard.data.model.toOrderStatus
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderStatus
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
import com.orderpush.app.features.order.data.model.appliedCount
import com.orderpush.app.features.order.presentation.viewmodel.OrderUiState
import com.orderpush.app.features.order.presentation.viewmodel.OrderViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDashboardScreen(
) {
    val viewModel= hiltViewModel<OrderViewModel>()
    val state=viewModel.uiState.collectAsState()
    val navigator= LocalNavigation.current
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var selectedTab by remember{mutableStateOf<OrderFilterTabMenu>(OrderFilterTabMenu.All)}
    val filterState=viewModel.filterState.collectAsState()

    val pendingOrders by remember {
        derivedStateOf {
            if( state.value is OrderUiState.Success) (state.value as OrderUiState.Success).orders.filter {
                it.status== OrderStatus.Received
            } else emptyList()
        }
    }
    var showOrderFilterDialog by remember { mutableStateOf(false) }
    val displayOrders by remember {
        derivedStateOf() {
            if (state.value is OrderUiState.Success) {
                (state.value as OrderUiState.Success).orders.filterByTab(selectedTab)
            }
            else emptyList()

        }
    }

    fun onOrderSelected(order: Order){
        if(viewModel.showOrdersAndDetailsView.value){
            selectedOrder = order
        }
        else{
            navigator.push(Screen.OrderDetails(order.id,))
        }
    }


    BaseView(
        title = "",
        leading = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBackIos,
                    modifier = Modifier.padding(start = 20.dp),
                    onClick = {
                        navigator.push(Screen.DashboardSelection)
                    }
                )
                DashboardOrderFilterTabView(
                    selected = selectedTab,
                    orders = if(state.value is OrderUiState.Success) (state.value as OrderUiState.Success).orders else emptyList(),
                    onClick = {
                        selectedTab = it
                    }
                )
            }
        },
        actions = {
            if (viewModel.showSearchBox.value) TextField(
                value = filterState.value.query?:"",
                onValueChange = {
                    viewModel.updateFilter(filterState.value.copy(query = it))

                }

            )
            ActionButton(
                icon =Icons.Default.Refresh,
                onClick = {
                    viewModel.fullSync()
                })
            ActionButton(
                icon = if(viewModel.showSearchBox.value)Icons.Default.Close else  Icons.Default.Search, onClick = {
                    viewModel.showSearchBox.value = !viewModel.showSearchBox.value
                    if(!viewModel.showSearchBox.value){
                        viewModel.updateFilter(filterState.value.copy(query = null))
                    }
                }
            )
            ActionButton(icon = Icons.Default.FilterList,
                count = filterState.value.appliedCount(),
                onClick = { showOrderFilterDialog = true })
            ActionButton(
                icon =if(viewModel.showOrdersAndDetailsView.value)Icons.AutoMirrored.Filled.MenuBook  else  Icons.AutoMirrored.Filled.List,
                onClick = {
                viewModel.showOrdersAndDetailsView.value =!viewModel.showOrdersAndDetailsView.value
            })

        }
    ) {

        Row(){
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
            ) {
                if (state.value is OrderUiState.Success) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        items(displayOrders) {
                            OrderTileView(
                                order = it,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                selected = it.id == selectedOrder?.id,
                                onClick = {
                                    onOrderSelected(it)
                                })
                        }
                    }
                }
                if (pendingOrders.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(pendingOrders, key = { it.id }) {
                            PendingOrderView(
                                order = it,
                                onClicked = {
                                    onOrderSelected(it)
                                },
                                modifier = Modifier
                                    .width(300.dp)
                                    .wrapContentHeight()
                                    .animateItem(),
                                onDecline = {
                                    viewModel.updateOrder(
                                        order = it, request = UpdateOrderRequest(
                                            status = OrderStatus.Cancelled
                                        )
                                    )
                                },
                                onAccept = {
                                    viewModel.updateOrder(
                                        order = it, request = UpdateOrderRequest(
                                            status = OrderStatus.Confirmed
                                        )
                                    )
                                }
                            )
                        }
                    }

                }
            }

            AnimatedVisibility(
                visible = selectedOrder!=null && viewModel.showOrdersAndDetailsView.value,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(600.dp)

            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(4.dp)
                            .background(Color.White)
                    ) { }
                    key(selectedOrder?.id) {
                        selectedOrder?.let {   OrderDetailsScreen(it.id) }
                    }
                }

            }
        }

        AnimatedVisibility(visible = showOrderFilterDialog) {
            BasicAlertDialog(
                onDismissRequest = {
                    showOrderFilterDialog = false
                }
            ) {
                OrderFilterDialogView(
                    filter = viewModel.filterState.collectAsState().value,
                    onChange = {
                        viewModel.updateFilter(it)
                    },
                    onClose = {
                        showOrderFilterDialog = false
                    }
                )
            }

        }
    }


}
@Composable
fun ActionButton(icon: ImageVector, modifier: Modifier= Modifier, onClick:()-> Unit,count:Int?=null){
    IconButton(
        modifier = modifier
            .size(46.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp)
            ),
        onClick = onClick
    ){
        Box(){
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface
            )
            if(count!=null && count>0)Box(
                modifier= Modifier.size(16.dp).background(MaterialTheme.colorScheme.onSurface,

                    CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("$count", style = MaterialTheme.typography.labelSmall.copy(
                     color = Color.Red
                ))
            }
        }
    }

}


