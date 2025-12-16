package com.orderpush.app.features.order.data.api

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderListResponse
import com.orderpush.app.features.order.data.model.UpdateOrderItemsPayload
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface OrderApi {
    @GET("orders")
    suspend fun getOrders(@QueryMap filters: Map<String, String>): Response<APIResponse<OrderListResponse>>

    @GET("orders/{id}")
    suspend fun getOrderDetails(
                          @Path("id") id:String,
                          ): Response<APIResponse<Order>>

    @PATCH("orders/{id}/order_items")
    suspend fun markOrderItemReady(
        @Path("id") id:String,
        @Body  payload:UpdateOrderItemsPayload): Response<APIResponse<Unit>>

    @PATCH("orders/{id}")
    suspend fun  updateOrder(@Path("id")orderId:String,@Body payload: UpdateOrderRequest):Response<APIResponse<Order>>
}