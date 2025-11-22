package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orderpush.app.core.extension.toUIColor
import com.orderpush.app.core.views.PopupMenuButton
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderItem

import com.orderpush.app.features.order.data.model.icon
import com.orderpush.app.features.order.data.model.isRecalled
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KdsOrderTileView(order: Order,
                     onItemReady:(OrderItem)->Unit,
                     onComplete:()->Unit,
                     onPrint:()->Unit,
                     onPriority:()->Unit,
                     onHold:()->Unit,
                     settings: KdsSettings
                     ) {
    val listState = rememberLazyListState()
    val items = order.orderItems ?: emptyList()
    val scope=rememberCoroutineScope()
    val canScrollDown by remember {
        derivedStateOf {
            listState.canScrollForward
        }
    }
    val canScrollUp by remember {
        derivedStateOf {
            listState.canScrollBackward
        }
    }
    Card(
        modifier = Modifier
            .width(260.dp)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            OrderHeaderView(order = order,settings)
            Spacer(modifier = Modifier.height(4.dp))


            Row {
                if(order.priorityAt!=null) {
                    PriorityTimerView(order)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .padding(bottom = 4.dp)

                )

                {
                    PopupMenuButton(
                        items = listOf("Hold"),
                        onItemClick = {
                            if(it=="Hold"){
                                onHold()
                            }
                        }
                    )
                    KdsCardButton(
                        onClick = onPriority,
                        label = "priority",
                        imageVector = Icons.Default.Star,
                        iconColor = if (order.priorityAt != null) Color(0xFFFBC02D) else null
                    )
                    KdsCardButton(
                        onClick = onPrint,
                        label = "print order",
                        imageVector = Icons.Default.Print
                    )
                    KdsCardButton(
                        onClick = onComplete,
                        label = "mark order ready",
                        imageVector = Icons.Default.Check
                    )
                }
            }

            Box(

            ){
                LazyColumn(
                    modifier = Modifier
                        .padding(8.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items.sortedBy { it.id }) { item ->
                        OrderItem(item, onItemReady,settings)
                    }
                }
                if (canScrollDown) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                    ){
                        ScrollIndicatorButton(
                            icon = Icons.Default.KeyboardArrowDown,
                            modifier = Modifier.align(Alignment.BottomCenter),
                            onClick = {
                                scope.launch {
                                    listState.animateScrollToItem(items.lastIndex)
                                }
                            }
                        )
                    }
                }
                if (canScrollUp) {
                    ScrollIndicatorButton(
                        icon = Icons.Default.KeyboardArrowUp,
                        modifier = Modifier.align(Alignment.TopCenter),
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                    )
                }
            }


        }
    }
}

@Composable
fun KdsCardButton(onClick: () -> Unit,imageVector: ImageVector,label:String,iconColor:Color?=null){
    Box(

        modifier = Modifier
            .height(30.dp)
            .width(30.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer, shape = CircleShape
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center

    ) {
        Icon(imageVector = imageVector,
            modifier = Modifier.size(20.dp),
            contentDescription = label, tint =iconColor?: MaterialTheme.colorScheme.onBackground,)
    }
}

@Composable
fun ScrollIndicatorButton(icon: ImageVector,onClick:()->Unit,modifier: Modifier){
    Icon(
        imageVector = icon,
        contentDescription = "More items above",
        modifier = modifier
            .padding(top = 16.dp)
            .size(32.dp)
            .background(
                color = Color.Black.copy(alpha = 0.6f),
                shape = CircleShape
            )
            .padding(4.dp)

            .clickable(
                onClick = onClick
            ),
        tint = Color.White
    )

}

@Composable
fun OrderHeaderView(order: Order,settings: KdsSettings){
    val now = remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now.value = Clock.System.now()
            delay(1000)
        }
    }
    val orderLocalTime = order.fulfillmentTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val nowLocalTime = now.value.toLocalDateTime(TimeZone.currentSystemDefault())
    val elapsedSeconds =orderLocalTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds- nowLocalTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds
    val safeElapsed = abs(elapsedSeconds)
    val hours = safeElapsed / 3600
    val minutes = (safeElapsed % 3600) / 60
    val seconds = safeElapsed % 60
    val formattedOrderTime = remember(order.fulfillmentTime) {
        val hour = if (orderLocalTime.hour % 12 == 0) 12 else orderLocalTime.hour % 12
        val amPm = if (orderLocalTime.hour < 12) "AM" else "PM"
        "%02d:%02d:%02d %s".format(hour, orderLocalTime.minute, orderLocalTime.second, amPm)
    }
    val totalMinutes= elapsedSeconds/60
    val backgroundColor = when {
        totalMinutes <= 0 -> settings.lateOrderColor
        totalMinutes in 1..10 -> settings.warningOrderColor
        else -> settings.onTimeOrderColor
    }
    Column(
        modifier = Modifier
            .background(backgroundColor.toUIColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "#${order.externalOrderId}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = settings.mainTextColor.toUIColor(),
                        fontSize = settings.fontSize.size.sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(order.storeCustomer?.name?:"",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = settings.mainTextColor.toUIColor(),
                    fontSize = settings.fontSize.size.sp
                )
            }
            if(order.isRecalled()) Icon(imageVector = Icons.Default.Refresh,
                tint = settings.mainTextColor.toUIColor(),
                contentDescription = order.mode.name,
                modifier = Modifier.size(14.dp)
            )
            Icon(imageVector = order.mode.icon(),
                tint = settings.mainTextColor.toUIColor(),
                contentDescription = order.mode.name,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        OrderTimeHeader(formattedOrderTime,
             hours,minutes,seconds,
            settings)
    }
}

@Composable
fun OrderTimeHeader(formattedOrderTime: String,
                    hours:Long,
                    minutes:Long,
                    seconds:Long,
                    settings: KdsSettings) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedOrderTime,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = settings.mainTextColor.toUIColor(),
                fontSize = settings.fontSize.size.sp,
            ),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = settings.mainTextColor.toUIColor(),
                fontSize = settings.fontSize.size.sp,
            ),
            fontWeight = FontWeight.Bold
        )

    }
}

@Composable
fun OrderItem(item: OrderItem, onReady: (OrderItem) -> Unit,settings: KdsSettings) {
    val textDecoration =if(item.ready==true) TextDecoration.LineThrough else TextDecoration.None
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                onClick = {
                    onReady(item)
                }
            )
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (item.ready == true) Color.Gray else Color.Black,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.qty.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = settings.mainTextColor.toUIColor(),
                    fontSize = settings.fontSize.size.sp,
                    textDecoration =textDecoration
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = settings.mainTextColor.toUIColor(),
                fontSize = settings.fontSize.size.sp,
                textDecoration =textDecoration

            )
            Spacer(modifier = Modifier.weight(1f))
//            if(item.synced==false) CircularProgressIndicator(
//                modifier = Modifier.size(14.dp).padding(start = 2.dp),
//                strokeWidth = 1.dp
//
//            )
        }

        item.options?.forEach { mod ->
            Text(
                text = mod.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = settings.modifierTextColor.toUIColor(),
                    fontSize = (settings.fontSize.size-2).sp,
                    textDecoration =textDecoration
                ),
                modifier = Modifier.padding(start = 32.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun PriorityTimerView(order: Order,modifier: Modifier= Modifier){
    val now = remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now.value = Clock.System.now()
            delay(1000)
        }
    }
    val priorityTime = order.priorityAt!!.toLocalDateTime(TimeZone.currentSystemDefault())
    val nowLocalTime = now.value.toLocalDateTime(TimeZone.currentSystemDefault())
    val elapsedSeconds = nowLocalTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds-priorityTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60

    Box(
        modifier  = modifier
            .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
            .background(Color(0xFFFBC02D))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterEnd
    ){
        Text(String.format("%02d:%02d:%02d", hours, minutes, seconds),
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.White
            )
        )
    }


}