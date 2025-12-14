package com.orderpush.app.features.dashboard.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.dashboard.presentation.viewmodel.DashboardSelectionViewModel

enum class DashboardType {
    OrderManager, KitchenManger,POS, MenuManger,
}

fun DashboardType.toScreen(): Screen? {
    return when (this) {
        DashboardType.OrderManager -> Screen.OrderDashboard
        DashboardType.KitchenManger -> Screen.KdsDashboard
        DashboardType.POS -> Screen.PosDashboard
       // DashboardType.DeliveryManager -> null
        DashboardType.MenuManger -> Screen.MenuManagerDashboard
    }
}

fun DashboardType.getIcon(): ImageVector {
    return when (this) {
        DashboardType.OrderManager -> Icons.Default.Dining
        DashboardType.KitchenManger -> Icons.Default.Restaurant
       // DashboardType.DeliveryManager -> Icons.Default.DeliveryDining
        DashboardType.MenuManger -> Icons.AutoMirrored.Filled.MenuBook
        DashboardType.POS -> Icons.Default.PointOfSale
    }
}

fun DashboardType.getColor(): Color {
    return when (this) {
        DashboardType.OrderManager -> Color(0xFF6366F1)
        DashboardType.KitchenManger -> Color(0xFF8B5CF6)
       // DashboardType.DeliveryManager -> Color(0xFF10B981)
        DashboardType.MenuManger -> Color(0xFFF59E0B)
        DashboardType.POS -> Color(0xFF009688)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardSelectionScreen() {
    val viewmodel = hiltViewModel<DashboardSelectionViewModel>()
    val navigator = LocalNavigation.current
    BaseView(
        title = "Select Your Dashboard"
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                columns = GridCells.FixedSize(500.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
            ) {
                items(DashboardType.entries) {
                    DashboardButton(
                        type = it,
                        onClick = {

                            val screen = it.toScreen()
                            if (screen != null) {
                                viewmodel.setDashboard(it)
                                navigator.replaceAll(screen)
                            }
                        },

                        )
                }

            }
        }
    }
}

@Composable
fun DashboardButton(
    modifier: Modifier= Modifier,
    type: DashboardType, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(

        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .background(
                        color = type.getColor().copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = type.getIcon(),
                    contentDescription = type.name,
                    tint = type.getColor(),
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = type.name
                        .replace(Regex("([a-z])([A-Z])"), "$1 $2"),
                     style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = getDescriptionForType(type),
                      style = MaterialTheme.typography.titleSmall
                )
            }

            Icon(
                imageVector = Icons.Default.Dining,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
            )
        }
    }
}

fun getDescriptionForType(type: DashboardType): String {
    return when (type) {
        DashboardType.OrderManager -> "Manage customer orders"
        DashboardType.KitchenManger -> "Kitchen operations"
        //DashboardType.DeliveryManager -> "Delivery tracking"
        DashboardType.MenuManger -> "Menu management"
        DashboardType.POS -> "Point of Sale"

    }
}