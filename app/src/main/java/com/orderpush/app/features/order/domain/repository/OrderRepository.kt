package com.orderpush.app.features.order.domain.repository

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderFilter
import com.orderpush.app.features.order.data.model.OrderItem
import com.orderpush.app.features.order.data.model.UpdateOrderItemRequest
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
import com.orderpush.app.features.order.data.repository.SocketOrderData
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun  syncOrders( filter: OrderFilter)
     fun  getOrders( filter: OrderFilter) : Flow<List<Order>>
    suspend fun  saveOrder(order: Order)
    suspend fun  getOrderDetails(id: String): APIResponse<Order>
    suspend fun getOrderDetailsFlow(id: String): Flow<Order?>
    fun subscribeOrders(): Flow<SocketOrderData>
    fun sendData(event:String,data: Any)
    fun socket(): Socket?
    suspend fun markOrderItemReady(order: Order,payload: List<UpdateOrderItemRequest>): APIResponse<Unit>
    suspend fun updateOrder(order: Order,payload: UpdateOrderRequest): APIResponse<Order>

}