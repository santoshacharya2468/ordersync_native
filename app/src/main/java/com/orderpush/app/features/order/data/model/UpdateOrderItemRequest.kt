package com.orderpush.app.features.order.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class  UpdateOrderItemRequest (
    val id:String,
    val ready:Boolean,
    @SerializedName("current_station_id")
    val currentStationId:String?=null,
    val status: OrderItemStatus?=null

    )


data class UpdateOrderItemsPayload(
    val items: List<UpdateOrderItemRequest>
)