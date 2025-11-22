package com.orderpush.app.features.order.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.datetime.Instant

data class UpdateOrderRequest(
    val status: OrderStatus?=null,
    val printed: Boolean?=null,
    @SerializedName("fulfillment_time")
    val fulfillmentTime:String?=null,
    val mode: OrderMode?=null,
    val priority:Int?=null,
    @SerializedName("hold_until")
    val holdUnit: Instant?=null,

)