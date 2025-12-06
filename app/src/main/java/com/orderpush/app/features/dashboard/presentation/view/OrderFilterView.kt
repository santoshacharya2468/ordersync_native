package com.orderpush.app.features.dashboard.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orderpush.app.features.dashboard.data.model.OrderFilterTabMenu
import com.orderpush.app.features.dashboard.data.model.toOrderStatus
import com.orderpush.app.features.order.data.model.Order


@Composable
fun OrderFilterView(selected: OrderFilterTabMenu,onClick:(OrderFilterTabMenu)-> Unit,
                     orders: List<Order>
                    ){
    Row(
        modifier = Modifier.padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OrderFilterTabMenu.entries.forEach {
            val isSelected = it == selected
            val orderStatus= it.toOrderStatus()
            val count= orders.filter {order->
                if(orderStatus==null) true
                else
                 order.status==orderStatus
            }.size
            Box(
                 modifier = Modifier
                     .padding(4.dp)
                     .height(height = 46.dp)
                     .background(
                         color = if (isSelected) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
                         shape = RoundedCornerShape(8.dp)
                     ).clickable{
                         onClick(it)
                     },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp)
                ){
                    Text(
                        it.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 6.dp)

                    )
                   if(count>0) Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color = Color.Gray.copy(alpha = 1f), shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ){
                        Text("$count", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

}