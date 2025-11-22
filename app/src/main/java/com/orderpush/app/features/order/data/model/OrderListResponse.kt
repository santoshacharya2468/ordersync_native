package com.orderpush.app.features.order.data.model

data class OrderListResponse(val orders: List<Order>,val lastSync:String)