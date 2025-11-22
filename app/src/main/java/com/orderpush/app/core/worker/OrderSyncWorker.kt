package com.orderpush.app.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.orderpush.app.features.order.data.model.OrderFilter
import com.orderpush.app.features.order.domain.repository.OrderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class OrderSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
private val orderRepository: OrderRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val filter = OrderFilter(
            )
          orderRepository.syncOrders(filter)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
