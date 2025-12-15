package com.orderpush.app.features.order.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderMode
import com.orderpush.app.features.order.data.model.OrderStatus
import com.orderpush.app.features.order.data.model.getFulfillmentTimeFormatted
import com.orderpush.app.features.order.data.model.icon
import com.orderpush.app.features.order.data.model.namePlaceHolder

@Composable
fun OrderTileView(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean=false,
    bottomContent: @Composable () -> Unit = {}

) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),


        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder(),
        colors = CardDefaults.cardColors(
            containerColor = if(selected) MaterialTheme.colorScheme.surfaceContainer else  MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight(Alignment.Top)
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Customer Avatar/Icon + Customer Name + Order ID + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Customer Avatar/Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = getOrderModeColor(order.mode),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = order.mode.icon(),
                        contentDescription = order.mode.toString(),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = order.storeCustomer?.name ?: "Guest",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "#${order.externalOrderId ?: order.id.take(8)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1
                        )
                    }
                    Text(order.getFulfillmentTimeFormatted(), style = MaterialTheme.typography.titleSmall)
                }

                Spacer(modifier = Modifier.weight(1f))
                OrderTimeRemainingView(order)
                StatusBadge(status = order.status)
            }
            OrderTypePriceCounterView(order)

            bottomContent()
        }
    }
}

@Composable
fun OrderTypePriceCounterView(order: Order){
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconLabelChip(
            icon = order.mode.icon(),
            label =order.mode.namePlaceHolder()
        )
        IconLabelChip(
            icon = Icons.Default.RestaurantMenu,
            label = "${order.orderItems?.size} items"
        )
        IconLabelChip(
            icon = Icons.Default.LocalOffer,
            label = "$${String.format("%.2f", order.total)}"
        )
    }
}


@Composable
fun IconLabelChip(icon: ImageVector, label: String){
    Row(
        modifier = Modifier
            .height(20.dp)
            .border(
                .5.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
     Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
        Text(label,
            style = MaterialTheme.typography.labelSmall
            )
    }
}

@Composable
private fun StatusBadge(status: OrderStatus) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.Received -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        OrderStatus.Hold -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        OrderStatus.Confirmed -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        OrderStatus.Preparing -> Color(0xFFFFF4E6) to Color(0xFFE65100)
        OrderStatus.Ready -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        OrderStatus.Picked_up -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        OrderStatus.Delivered -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        OrderStatus.Completed -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        OrderStatus.Cancelled -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.name.replace("_", " "),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            fontSize = 11.sp
        )
    }
}

@Composable
fun getOrderModeColor(mode: OrderMode): Color {
    return when (mode) {
        OrderMode.Delivery -> Color(0xFFFF6B35)
        OrderMode.Pickup -> Color(0xFFDC143C)
        OrderMode.Dine_In -> Color(0xFF2E7D32)
    }
}