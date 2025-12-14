package com.orderpush.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.kds.data.model.Station
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderMode
import com.orderpush.app.features.order.data.model.OrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


@Dao
interface OrderDao {

    @Query("""
    SELECT * FROM `order`
    WHERE (:fulfillmentFrom IS NULL OR fulfillmentTime >= :fulfillmentFrom)
      AND (:fulfillmentTo IS NULL OR fulfillmentTime <= :fulfillmentTo)
      AND (:mode IS NULL OR mode = :mode)
     AND (:statusListSize = 0 OR status IN (:statusList))
    ORDER BY
        CASE WHEN priorityAt IS NULL THEN 1 ELSE 0 END, 
        priorityAt ASC, fulfillmentTime ASC
""")
    fun getOrders(
        fulfillmentFrom: Instant?,
        fulfillmentTo: Instant?,
        mode: OrderMode?,
        statusList: List<OrderStatus> = emptyList(),
        statusListSize: Int = statusList.size
    ): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  saveOrder(order: Order)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  saveOrders(orders: List<Order>)

    @Query("SELECT * FROM `order` WHERE id = :id")
     fun  getOrderDetails(id:String): Flow<Order?>


}
