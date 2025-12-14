package com.orderpush.app.features.order.data.model

import KotlinxInstantAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.orderpush.app.features.customer.data.model.Customer
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


enum class OrderMode{
    Delivery,Pickup,Dine_In
}

fun OrderMode.namePlaceHolder():String{
      return  this.name.replace("_"," ")
}

enum  class OrderStatus{
    Received, Confirmed,Hold, Preparing, Ready, Picked_up, Delivered, Cancelled,Completed
}

enum class OrderItemStatus{
    processing,ready,recall
}
@Entity
data class Order(
    @PrimaryKey val id: String,
    val storeId: String,
    val customerId: String,
    val externalOrderId: String?,
    val mode: OrderMode,
    val orderTime: Instant,
    val fulfillmentTime: Instant,
    val deliveryAddress: DeliveryAddress?,
    val printed: Boolean,
    val subtotal: Double,
    val deliveryFee: Double,
    val taxAmount: Double,
    val tipAmount: Double,
    val total: Double,
    val discount: Double,
    val status: OrderStatus,
    val terminal: String?,
    val displayNumber: String?,
    val priority: Int,
    val priorityAt:Instant?=null,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: String,
    val storeCustomer: Customer?,
    val orderItems: List<OrderItem>?,
    val synced:Boolean?,
    val holdUntil: Instant?=null
)

data class DeliveryAddress(
    val city: String?,
    val state: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("post_code") val postCode: String?,
    @SerializedName("street_address") val streetAddress: String?,
    @SerializedName("formatted_address") val formattedAddress: String?
)


data class OrderItem(
    val id:String,
    val name:String,
    val price: Double,
    @SerializedName("order_id")
    val orderId:String,
    val qty: Int,
    val discount: Double?,
    @SerializedName("unit_price")
    val unitPrice: Double?,
    val notes: String?,
    val ready: Boolean?,
    @SerializedName("mods")
    val options: List<OrderItemOption>?,
    val status:OrderItemStatus=OrderItemStatus.processing,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("current_station_id")
    val currentStationId: String? = null,
    val station:String?=null,
    val synced :Boolean?=null
    )


data class OrderItemOption(val id:String?,val name:String)


fun decodeOrder(input: String): Order{
   return  GsonBuilder()
        .registerTypeAdapter(Instant::class.java, KotlinxInstantAdapter())
        .create()
        .fromJson(input, Order::class.java)

}


fun OrderMode.icon():ImageVector{
    return when(this){
        OrderMode.Delivery -> Icons.Default.DeliveryDining
        OrderMode.Pickup -> Icons.Default.ShoppingBag
        OrderMode.Dine_In -> Icons.Default.Dining
    }

}

fun Order.isRecalled(): Boolean{
    return this.orderItems?.firstOrNull{it.status== OrderItemStatus.recall }!=null
}


fun Order.getFulfillmentTimeFormatted(): String {
    val localDateTime = this.fulfillmentTime
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val hour = localDateTime.hour
    val minute = localDateTime.minute
    val period = if (hour >= 12) "pm" else "am"
    val displayHour = if (hour % 12 == 0) 12 else hour % 12

    return String.format("%02d:%02d %s", displayHour, minute, period)
}


fun Order.nextStepStatus(): Pair< OrderStatus? , String?>{
    return when(this.status){
        OrderStatus.Cancelled-> Pair(null , null)
        OrderStatus.Received -> Pair(OrderStatus.Confirmed,"Accept")
        OrderStatus.Confirmed -> Pair(OrderStatus.Preparing,"Start preparing")
        OrderStatus.Hold -> Pair(OrderStatus.Confirmed,"Release hold")
        OrderStatus.Preparing -> Pair(OrderStatus.Ready,"Mark as ready")
        OrderStatus.Ready -> Pair(OrderStatus.Completed,"Mark as completed")
        OrderStatus.Picked_up -> Pair(null , null)
        OrderStatus.Delivered -> Pair(null , null)
        OrderStatus.Completed -> Pair(null , null)
    }
}

