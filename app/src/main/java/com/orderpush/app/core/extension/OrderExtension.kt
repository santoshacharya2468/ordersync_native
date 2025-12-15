package com.orderpush.app.core.extension

import com.orderpush.app.features.dashboard.data.model.OrderFilterTabMenu
import com.orderpush.app.features.dashboard.data.model.toOrderStatus
import com.orderpush.app.features.order.data.model.Order

fun List<Order>.filterByTab(tabMenu: OrderFilterTabMenu): List<Order>{
    val orderStatus= tabMenu.toOrderStatus()
  return  this.filter {order->
        if(orderStatus==null) true
        else
            order.status==orderStatus
    }
}