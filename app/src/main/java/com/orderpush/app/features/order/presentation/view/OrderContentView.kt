package com.orderpush.app.features.order.presentation.view
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.extension.format
import com.orderpush.app.core.views.AppButton
import com.orderpush.app.core.views.AppButtonVariant
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderItem
import com.orderpush.app.features.order.data.model.OrderMode
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
import com.orderpush.app.features.order.data.model.nextStepStatus
import com.orderpush.app.features.order.presentation.viewmodel.OrderViewModel
import com.orderpush.app.features.printer.presentation.viewmodel.PrinterSelectionViewModel

enum class TabMenu{
    Order,Customer,Timeline
}
@Composable
fun OrderContentView(
    order: Order
) {

    var currentTab by remember { mutableStateOf(TabMenu.Order) }
    val printerViewModel= hiltViewModel<PrinterSelectionViewModel>()
    val orderViewModel=hiltViewModel<OrderViewModel>()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .padding(horizontal = 4.dp)

    ) {
        Box {
            OrderTileView(
                order = order,
                selected = true,
                bottomContent = {
                    if (!order.notes.isNullOrEmpty())Text("Notes:${order.notes}")
                },

                onClick = {

                })
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Spacer(modifier = Modifier.height(5.dp))
                OrderTabMenuView(
                    onClick = {
                        currentTab = it
                    },
                    selected = currentTab
                )
                AnimatedContent(
                    targetState = currentTab,
                    modifier = Modifier
                        .fillMaxSize()


                ) {
                    when (it) {
                        TabMenu.Order -> OrderTabView(order)
                        TabMenu.Customer -> CustomerTabView(order)
                        TabMenu.Timeline -> TimelineTabView(order)
                    }

                }
            }

        }
        val nextStatus= order.nextStepStatus()
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AppButton(text = "",
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer,
                    RoundedCornerShape(10.dp)
                    ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                },
                variant = AppButtonVariant.Secondary,
                onClick = {

                })
            AppButton(text = "",
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer,RoundedCornerShape(10.dp)),
                leadingIcon = {

                Icon(imageVector = Icons.Default.Print, contentDescription = null)
            },
                variant = AppButtonVariant.Secondary,
                onClick = {
                    printerViewModel.printReceipt(order)

            })
             if(nextStatus.first!=null && nextStatus.second!=null)   AppButton(
                    text = nextStatus.second!!,
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }, onClick = {
                     orderViewModel.updateOrder(order, UpdateOrderRequest(
                          status = nextStatus.first
                     ))
                    })

        }


    }

}


@Composable
fun OrderTabView(order: Order){
    Column {
        Text("Items", style = MaterialTheme.typography.titleLarge)
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)

        ) {
            items(order.orderItems!!){
                OrderItemView( it)
            }

        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ){
            PriceView("Subtotal", order.subtotal)
            if (order.mode == OrderMode.Delivery) PriceView("Delivery Fee", order.deliveryFee)
            if (order.tipAmount > 0) PriceView("Tax", order.taxAmount)
            if (order.tipAmount > 0) PriceView("Tip", order.tipAmount)
            if (order.discount > 0) PriceView("Discount", order.discount)
            PriceView("Total", order.total)
        }

    }
}

@Composable
fun PriceView(label: String, price: Double){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)

        Text("$${String.format("%.2f", price)}")
    }
}


@Composable
fun CustomerTabView(order: Order){
    val context= LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("${order.storeCustomer?.name}")

      if(!order.storeCustomer?.phone.isNullOrEmpty())  Text(
          order.storeCustomer.phone,
             modifier = Modifier.clickable{
                 val intent = Intent(Intent.ACTION_DIAL).apply {
                     data ="tel:${order.storeCustomer.phone}".toUri()
                 }
                 context.startActivity(intent)
             }
            )
    }
}

@Composable
fun TimelineTabView(order: Order) {
    val placedAt by remember {
       mutableStateOf( order.createdAt
            .format("EEE MMM d hh:mm:ss a"))
    }
    val fulfilledAt by remember(order.fulfillmentTime) {
        mutableStateOf( order.fulfillmentTime
            .format("EEE MMM d hh:mm:ss a"))
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DateRowView("Received", placedAt)
        DateRowView("Fulfilled", fulfilledAt)
    }
}

@Composable
fun DateRowView(label: String, date: String){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)

        Text(date)
    }
}


@Composable
fun OrderTabMenuView(
    onClick: (TabMenu) -> Unit,
    selected: TabMenu
){

    Row(
        modifier = Modifier.fillMaxWidth(),
         horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TabMenu.entries.forEach { menu->
            Box(
                modifier = Modifier
                    .border(
                        1.dp, Color.Gray,
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        color = if (menu == selected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,

                        )
                    .clickable {
                        onClick(menu)
                    }
                    .padding(8.dp)
                    ,
                contentAlignment = Alignment.Center

                )

             {
                Text(menu.name)
            }

        }
    }

}


@Composable
fun OrderItemView(item: OrderItem){
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("${item.qty} x ${item.name}")
            Text("$${item.price}")
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (!item.notes.isNullOrEmpty())Text("     note:${item.notes}", style = MaterialTheme.typography.labelSmall)

    }
}

