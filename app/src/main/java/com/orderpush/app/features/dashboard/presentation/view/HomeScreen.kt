package com.orderpush.app.features.dashboard.presentation.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.orderpush.app.features.order.data.model.OrderStatus
import com.orderpush.app.features.order.presentation.view.PendingOrderView
import com.orderpush.app.features.order.presentation.viewmodel.OrderUiState
import com.orderpush.app.features.order.presentation.viewmodel.OrderViewModel
import com.orderpush.app.features.order.presentation.view.OrderTileView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import com.orderpush.app.features.order.data.model.UpdateOrderRequest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
) {
    val viewModel= hiltViewModel<OrderViewModel>()
    val state=viewModel.uiState.collectAsState()
    val navigator= LocalNavigation.current
    var selectedTab = remember{mutableStateOf<OrderFilterTabMenu>(OrderFilterTabMenu.All)}
    val pendingOrders=remember {
        derivedStateOf {
           if( state.value is OrderUiState.Success) (state.value as OrderUiState.Success).orders.filter {
               it.status== OrderStatus.Received
           } else emptyList()
        }
    }
    BaseView(
        title = "",
        leading = {
            Row {
                ActionButton(
                    icon = Icons.Default.Home,
                    modifier = Modifier.padding(start = 20.dp),
                    onClick = {

                    }
                )
                TextButton(
                   onClick = {

                   }
                ) {
                    Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "up")
                    Text(text = "Active", style = MaterialTheme.typography.titleLarge)
                }
                OrderFilterView(
                    selected = selectedTab.value,
                    orders = if(state.value is OrderUiState.Success) (state.value as OrderUiState.Success).orders else emptyList(),
                    onClick = {
                        selectedTab.value = it
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

             ActionButton(icon = Icons.Default.Add) { }

             ActionButton(icon = Icons.Default.MenuBook) { }
         }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ){
            if (state.value is OrderUiState.Success) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    items((state.value as OrderUiState.Success).orders) {
                        OrderTileView(order = it, onClick = {
                            navigator.push(Screen.OrderDetails(it.id))
                        })
                    }
                }
            }
            if(pendingOrders.value.isNotEmpty()){
                LazyRow(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(10.dp),
                     horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pendingOrders.value, key = { it.id }) {
                        PendingOrderView(order = it,
                           modifier = Modifier.width(300.dp).wrapContentHeight().animateItem(),
                            onDecline = {
                                viewModel.updateOrder(order = it, request = UpdateOrderRequest(
                                    status = OrderStatus.Cancelled
                                ))
                            },
                            onAccept = {
                                viewModel.updateOrder(order = it, request = UpdateOrderRequest(
                                     status = OrderStatus.Confirmed
                                ))
                            }
                            )
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
             .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp)),
        onClick = onClick
    ){
        Icon(imageVector = icon, contentDescription = "", tint = Color.Black)
    }

}


