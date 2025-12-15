package com.orderpush.app.features.order.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orderpush.app.core.extension.filterByTab
import com.orderpush.app.features.dashboard.data.model.OrderFilterTabMenu
import com.orderpush.app.features.dashboard.data.model.toOrderStatus
import com.orderpush.app.features.order.data.model.Order

@Composable
fun DashboardOrderFilterTabView(selected: OrderFilterTabMenu, onClick:(OrderFilterTabMenu)-> Unit,
                    orders: List<Order>
){
    Row(
        modifier = Modifier.padding(horizontal = 6.dp),

    ) {
        OrderFilterTabMenu.entries.forEach {
            val isSelected = it == selected
            val count= orders.filterByTab(it).size
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .height(height = 46.dp)
                    .defaultMinSize(minWidth = 70.dp)

                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ).clickable {
                        onClick(it)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                 modifier = Modifier.padding(horizontal = 8.dp)

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
//                    else Spacer(modifier = Modifier.size(20.dp))
                }
            }
        }
    }

}