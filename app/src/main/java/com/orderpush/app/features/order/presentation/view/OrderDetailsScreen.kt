@file:OptIn(ExperimentalMaterial3Api::class)

package com.orderpush.app.features.order.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.order.data.model.*
import com.orderpush.app.features.order.presentation.viewmodel.OrderDetailsUiState
import com.orderpush.app.features.order.presentation.viewmodel.OrderDetailsViewModel
@Composable
fun OrderDetailsScreen( orderId: String)  {
        val viewModel = hiltViewModel<OrderDetailsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.getOrderDetails(orderId)
        }

    BaseView(title = "Order details") {
        when (uiState) {
            OrderDetailsUiState.Idle,
            OrderDetailsUiState.Loading -> {}
            is OrderDetailsUiState.Success -> {
                val order = (uiState as OrderDetailsUiState.Success).order
                OrderContent(order)
            }
            is OrderDetailsUiState.Error -> {}
        }
    }

}

@Composable
fun OrderContent(
    order: Order,
    onMarkReady: () -> Unit = {},
    onPrint: () -> Unit = {},
    onMoreOptions: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        OrderDetailsHeader(order = order)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            OrderTabs()
            Spacer(Modifier.height(16.dp))

            if (!order.notes.isNullOrEmpty()) {
                NotesSection(notes = order.notes)
                Spacer(Modifier.height(16.dp))
            }

            OrderItemsList(items = order.orderItems ?: emptyList())
            Spacer(Modifier.height(16.dp))

            TransactionDetails(order = order)
            Spacer(Modifier.height(80.dp))
        }

        BottomActionButtons(onMarkReady, onPrint, onMoreOptions)
    }
}

@Composable
fun OrderDetailsHeader(order: Order) {
    val cs = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = cs.surface,
        tonalElevation = 2.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = cs.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = cs.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp).size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = order.storeCustomer?.name ?: "Guest",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface
                        )
                        Text(
                            text = order.id.take(6),
                            fontSize = 14.sp,
                            color = cs.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = cs.surfaceVariant,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "3 min",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = cs.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = order.mode.icon(),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = cs.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = order.mode.name.replace("_", "-"),
                    fontSize = 14.sp,
                    color = cs.onSurfaceVariant
                )

                Spacer(Modifier.width(16.dp))
                Text(
                    text = "$${String.format("%.2f", order.total)}",
                    fontSize = 14.sp,
                    color = cs.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = cs.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${order.orderItems?.size ?: 0} items",
                    fontSize = 14.sp,
                    color = cs.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OrderTabs() {
    val cs = MaterialTheme.colorScheme
    val tabs = listOf("Order", "Courier", "Eater", "Timeline")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == "Order"
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) cs.primary else cs.surfaceVariant,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tab,
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (isSelected) cs.onPrimary else cs.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun NotesSection(notes: String) {
    val cs = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = cs.secondaryContainer,
        border = BorderStroke(1.dp, cs.outlineVariant)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.StickyNote2,
                contentDescription = null,
                tint = cs.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = "Note",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = cs.onSurfaceVariant
                )
                Text(
                    text = "\"$notes\"",
                    fontSize = 14.sp,
                    color = cs.onSurface
                )
            }
        }
    }
}

@Composable
fun OrderItemsList(items: List<OrderItem>) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = cs.surface
    ) {
        Column(Modifier.padding(16.dp)) {
            items.forEachIndexed { i, item ->
                OrderItemRow(item)
                if (i != items.lastIndex) Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.weight(1f)) {
            Text(
                text = item.qty.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = cs.onSurface,
                modifier = Modifier.width(30.dp)
            )
            Column {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = cs.onSurface
                )
                item.options?.forEach {
                    Text(
                        text = "• ${it.name}",
                        fontSize = 13.sp,
                        color = cs.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                if (!item.notes.isNullOrEmpty()) {
                    Text(
                        text = "Note: ${item.notes}",
                        fontSize = 13.sp,
                        color = cs.tertiary,
                        modifier = Modifier.padding(top = 4.dp),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionDetails(order: Order) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = cs.surface
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Transaction details",
                fontSize = 14.sp,
                color = cs.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))
            TransactionRow("Subtotal", order.subtotal)
            Spacer(Modifier.height(8.dp))
            TransactionRow("Bag Fee", order.deliveryFee)

            if (order.taxAmount > 0) {
                Spacer(Modifier.height(8.dp))
                TransactionRow("Tax", order.taxAmount)
            }
            if (order.tipAmount > 0) {
                Spacer(Modifier.height(8.dp))
                TransactionRow("Tip", order.tipAmount)
            }
            if (order.discount > 0) {
                Spacer(Modifier.height(8.dp))
                TransactionRow("Discount", -order.discount, isDiscount = true)
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = cs.outlineVariant)
            Spacer(Modifier.height(12.dp))

            TransactionRow("Total", order.total, isTotal = true)
        }
    }
}

@Composable
fun TransactionRow(
    label: String,
    amount: Double,
    isTotal: Boolean = false,
    isDiscount: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) cs.onSurface else cs.onSurfaceVariant
        )
        Text(
            text = if (isDiscount)
                "-$${String.format("%.2f", -amount)}"
            else
                "$${String.format("%.2f", amount)}",
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
            color = if (isDiscount) cs.error else cs.onSurface
        )
    }
}

@Composable
fun BottomActionButtons(
    onMarkReady: () -> Unit,
    onPrint: () -> Unit,
    onMoreOptions: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        color = cs.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onMoreOptions,
                modifier = Modifier
                    .size(56.dp)
                    .background(cs.surfaceVariant, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.MoreVert, null, tint = cs.onSurface)
            }

            IconButton(
                onClick = onPrint,
                modifier = Modifier
                    .size(56.dp)
                    .background(cs.surfaceVariant, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Print, null, tint = cs.onSurface)
            }

            Button(
                onClick = onMarkReady,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cs.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Check, null, tint = cs.onPrimary)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Mark ready",
                    color = cs.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
