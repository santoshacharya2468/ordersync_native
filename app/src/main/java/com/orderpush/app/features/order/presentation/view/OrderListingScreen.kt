package com.orderpush.app.features.order.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.AppBarAction
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.core.views.SegmentedTabView
import com.orderpush.app.features.dashboard.data.model.OrderFilterTabMenu
import com.orderpush.app.features.dashboard.data.model.toOrderFilterTabMenu
import com.orderpush.app.features.dashboard.data.model.toOrderStatus
import com.orderpush.app.features.dashboard.presentation.view.DashboardDrawer
import com.orderpush.app.features.order.data.model.OrderFilter
import com.orderpush.app.features.order.presentation.ui.OrderDetailsScreen
import com.orderpush.app.features.order.presentation.viewmodel.OrderUiState
import com.orderpush.app.features.order.presentation.viewmodel.OrderViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListingScreen()
{
    val viewModel: OrderViewModel = hiltViewModel()
        val orderState by viewModel.uiState.collectAsState()
        var showOrderFilterDialog by remember { mutableStateOf(false) }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigation.current
        val filter= viewModel.filterState.collectAsState()
        DashboardDrawer(
           drawerState=drawerState,
        ) {
            BaseView(
            title = "Orders",
            leading = {
                IconButton(
                    onClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                }
            },

        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedVisibility(showOrderFilterDialog) {
                    OrderFilterDialogView(filter = filter.value,
                         onClose = {
                             showOrderFilterDialog=false
                         },
                         onChange = {
                             viewModel.updateFilter(it.copy(page = 1,
                                 updatedAt = Clock.System.now().epochSeconds
                                 ))
                         }
                        )
                }
                OrderFilterSection(
                    currentFilter = filter.value,
                    onFilterChanged = { newFilter ->
                       viewModel.updateFilter( newFilter.copy(page = 1))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                when (orderState) {
                    is OrderUiState.Idle -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    "No orders found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    "Try adjusting your filters",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    is OrderUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is OrderUiState.Success -> {
                        val orders = (orderState as OrderUiState.Success).orders
                        if (orders.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "No orders match your filters",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    TextButton(
                                        onClick = {
                                           viewModel.updateFilter( filter.value.copy(

                                                mode = null,
                                                maxTotal = null,
                                                page = 1
                                            ))
                                        }
                                    ) {
                                        Text("Clear Filters")
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(orders, key = {it.id}) { order ->
                                    var visible by remember { mutableStateOf(false) }

                                    LaunchedEffect(order.id) {
                                        visible = true
                                    }
                                    AnimatedVisibility(
                                        visible = visible,
                                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                                    ) {
                                        OrderTileView(
                                            order = order,
                                            onClick = {
                                                navigator.push(Screen.OrderDetails(order.id))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is OrderUiState.Error -> {
                        val message = (orderState as OrderUiState.Error).message
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "Something went wrong",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(
                                    onClick = { viewModel.updateFilter(filter.value.copy(page = 1)) }
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}




@Composable
fun OrderFilterSection(
    currentFilter: OrderFilter,
    onFilterChanged: (OrderFilter) -> Unit,
    modifier: Modifier = Modifier
) {

        Column(
            modifier
        ) {
            SegmentedTabView<OrderFilterTabMenu>(
                tabs = OrderFilterTabMenu.entries.toList(),
                selectedValue = currentFilter.statues.firstOrNull().toOrderFilterTabMenu(),
                onTabSelected = {

                },
                modifier = Modifier
                    .fillMaxWidth()

            )
        }

}


