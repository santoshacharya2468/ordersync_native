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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.dashboard.data.model.OrderFilterTabMenu
import com.orderpush.app.features.dashboard.data.model.toOrderStatus
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderStatus
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
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
    val pendingOrders by remember {
        derivedStateOf {
            if( state.value is OrderUiState.Success) (state.value as OrderUiState.Success).orders.filter {
                it.status== OrderStatus.Received
            } else emptyList()
        }
    }
    var showOrderFilterDialog by remember { mutableStateOf(false) }
    AnimatedVisibility(visible = showOrderFilterDialog) {
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
    BaseView(
        title = "",
        leading = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = Icons.Default.Home,
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
                        viewModel.updateFilter(
                            viewModel.filterState.value.copy(
                                statues = if(it.toOrderStatus()!=null)listOf(it.toOrderStatus()!!)  else emptyList()
                            )
                        )
                    }
                )
            }
        },
        actions = {
            ActionButton(
                icon = Icons.Default.Search, onClick = {

                }
            )

            ActionButton(icon = Icons.Default.FilterList) { showOrderFilterDialog = true }

            ActionButton(icon = Icons.AutoMirrored.Filled.MenuBook) { }
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
                        items((state.value as OrderUiState.Success).orders) {
                            OrderTileView(
                                order = it,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                selected = it.id == selectedOrder?.id,
                                onClick = {
                                    selectedOrder = it
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
                visible = selectedOrder!=null,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(600.dp)

            ) {
                Row {
                    Box(
                        modifier = Modifier.fillMaxHeight().width(4.dp).background(Color.White)
                    ) { }
                    key(selectedOrder?.id) {
                        selectedOrder?.let {   OrderDetailsScreen(it.id) }
                    }
                }

            }
        }
    }
}
@Composable
fun ActionButton(icon: ImageVector, modifier: Modifier= Modifier, onClick:()-> Unit){
    IconButton(
        modifier = modifier
            .size(46.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp)
            ),
        onClick = onClick
    ){
        Icon(imageVector = icon, contentDescription = "", tint = MaterialTheme.colorScheme.onSurface)
    }

}


