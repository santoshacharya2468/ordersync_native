package com.orderpush.app.core.di

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.orderpush.app.core.network.NetworkConfiguration
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.core.worker.SyncOrderUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class OrderPushApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: SyncOrderWorkerFactory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


}

class SyncOrderWorkerFactory @Inject constructor(
     private val sessionManager: SessionManager,
    private val networkConfiguration: NetworkConfiguration,

) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = SyncOrderUpdateWorker(
        appContext,
        workerParameters,
        sessionManager,
        networkConfiguration

    )

}