package com.orderpush.app.features.dashboard.data.model

import com.orderpush.app.features.order.data.model.OrderStatus

enum class OrderFilterTabMenu{
    All,Pending,Completed,Ready,Cancelled
}
fun OrderFilterTabMenu.toOrderStatus():OrderStatus?{
    return when(this){
        OrderFilterTabMenu.All -> null
        OrderFilterTabMenu.Pending -> OrderStatus.Received
        OrderFilterTabMenu.Ready -> OrderStatus.Ready
        OrderFilterTabMenu.Cancelled -> OrderStatus.Cancelled
        OrderFilterTabMenu.Completed -> OrderStatus.Completed
    }
}

fun OrderStatus?.toOrderFilterTabMenu():OrderFilterTabMenu{
    return when(this){
        OrderStatus.Received -> OrderFilterTabMenu.Pending
        OrderStatus.Ready -> OrderFilterTabMenu.Ready
        OrderStatus.Cancelled -> OrderFilterTabMenu.Cancelled
        OrderStatus.Completed-> OrderFilterTabMenu.Completed
        null -> OrderFilterTabMenu.All
        else -> OrderFilterTabMenu.All
    }
}