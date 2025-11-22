package com.orderpush.app.features.order.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.orderpush.app.core.database.OrderDao
import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.core.network.NetworkConfiguration
import com.orderpush.app.core.services.SocketManager
import com.orderpush.app.core.services.SocketOrderEvent
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.core.worker.enqueueOrderUpdateTask
import com.orderpush.app.features.order.data.api.OrderApi
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderFilter
import com.orderpush.app.features.order.data.model.UpdateOrderItemRequest
import com.orderpush.app.features.order.data.model.UpdateOrderItemsPayload
import com.orderpush.app.features.order.data.model.UpdateOrderRequest
import com.orderpush.app.features.order.data.model.decodeOrder
import com.orderpush.app.features.order.data.model.toDefaultFromDate
import com.orderpush.app.features.order.data.model.toDefaultToDate
import com.orderpush.app.features.order.data.model.toQueryMap
import com.orderpush.app.features.order.domain.repository.OrderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import okio.IOException
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.map

sealed class  SocketOrderData {
   data  class OrderCreated(val order: Order) : SocketOrderData()
    data class OrderUpdated(val order: Order): SocketOrderData()
}


class OrderRepositoryImpl @Inject constructor(private val api: OrderApi,
    private  val sessionManager: SessionManager,
    private  val socketManager: SocketManager,
    private  val networkConf: NetworkConfiguration,
    private  val orderDao: OrderDao,
    private  val gson: Gson,
   @ApplicationContext private  val context: Context
    ) : OrderRepository   {
    override suspend fun syncOrders(filter: OrderFilter){
        val response= api.getOrders(filter.toQueryMap())
        if(response.isSuccessful){
            sessionManager.setLastSyncDate(response.body()?.data?.lastSync?:"")
            orderDao.saveOrders(response.body()?.data?.orders?: emptyList())
        }
    }

    override  fun getOrders(filter: OrderFilter): Flow<List<Order>> {
        return orderDao.getOrders(
            fulfillmentFrom = filter.toDefaultFromDate(),
            fulfillmentTo = filter.toDefaultToDate(),
            statusList = filter.statues,
            mode = filter.mode).map { orders->
            val filteredOrders = orders
                .filter { order ->
                    if (filter.station != null) {
                        order.orderItems?.any { it.currentStationId == filter.station } == true
                    } else true
                }
                .map { order ->
                    if (filter.station != null) {
                        order.copy(
                            orderItems = order.orderItems?.filter { it.currentStationId == filter.station } ?: emptyList()
                        )
                    } else order
                }

            filteredOrders
        }


    }

    override suspend fun saveOrder(order: Order) {
        orderDao.saveOrder(order)
    }

    override suspend fun getOrderDetails(id: String): APIResponse<Order> {
        val response= api.getOrderDetails(id,)
        return  response.toApiResponse()
    }
  override  fun subscribeOrders(): Flow<SocketOrderData> {
      socketManager.init(networkConf.socketUrl)
      return socketManager.orderEvents
          .map { event ->
              when (event) {
                  is SocketOrderEvent.OrderCreated -> {
                      val order = decodeOrder(event.data.toString())
                      SocketOrderData.OrderCreated(order)
                  }

                  is SocketOrderEvent.OrderUpdated -> {
                      val order = decodeOrder(event.data.toString())
                      SocketOrderData.OrderUpdated(order)
                  }
              }
          }
          .catch { e ->
              Log.e("OrderRepository", "Error in subscribeOrders", e)
          }
  }

    override fun sendData(event: String, data: Any) {
       socketManager.emit(event,data)

    }

    override fun socket(): Socket? {
       return  socketManager.getSocket()
    }

    override suspend fun markOrderItemReady(order: Order,request: List<UpdateOrderItemRequest>): APIResponse<Unit> {
        val updatedItems = order.orderItems?.map { existingItem ->
             val itemChanged=request.firstOrNull { it.id==existingItem.id }
            if (itemChanged!=null) {
                existingItem.copy(
                    ready = itemChanged.ready,
                    synced = false,
                    currentStationId = itemChanged.currentStationId?:existingItem.currentStationId,
                    status = itemChanged.status?:existingItem.status
                )
            } else {
                existingItem
            }
        }
        saveOrder(order.copy(
            orderItems =updatedItems
        ))
         try {
             return api.markOrderItemReady(
                 id = order.id,
                 payload = UpdateOrderItemsPayload(request)
             ).toApiResponse()
         }
         catch (_: IOException){
             enqueueOrderUpdateTask(context=context, method = "PATCH", url ="orders/${order.id}/order_items", body =gson.toJson(UpdateOrderItemsPayload(request))  )
             return APIResponse(status = 500, data = null, message = null)
         }


    }

    override suspend fun updateOrder(
        order: Order,
        request: UpdateOrderRequest
    ):APIResponse<Order> {
        saveOrder(
            order.copy(
                status = request.status ?: order.status,
                printed = request.printed ?: order.printed,
                priority = request.priority ?: order.priority,
                mode = request.mode ?: order.mode,
                synced = false,
                priorityAt = if(request.priority==null)order.priorityAt else if(request.priority==2) Clock.System.now() else null,
                fulfillmentTime = if (request.fulfillmentTime != null) Instant.parse(request.fulfillmentTime) else order.fulfillmentTime
            )
        )
        try {
            return api.updateOrder(order.id, request)
                .toApiResponse()
        } catch (e: IOException) {
            enqueueOrderUpdateTask(
                context = context,
                method = "PATCH",
                url = "orders/${order.id}",
                body = gson.toJson(request)
            )

            return APIResponse(status = 500, data = null, message = null)
        }
    }


}