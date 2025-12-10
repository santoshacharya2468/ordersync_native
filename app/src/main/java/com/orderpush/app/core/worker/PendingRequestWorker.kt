package com.orderpush.app.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.orderpush.app.core.network.AuthInterceptor
import com.orderpush.app.core.network.NetworkConfiguration
import com.orderpush.app.core.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
@HiltWorker
class SyncOrderUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private  val sessionManager: SessionManager,
    private  val networkConfiguration: NetworkConfiguration
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        Log.d("OrderSync","order sync")
        val method = inputData.getString("method") ?: return Result.failure()
        val url = inputData.getString("url") ?: return Result.failure()
        val body = inputData.getString("body")
        return try {
            val client = OkHttpClient.Builder()
                .addInterceptor(
                    AuthInterceptor(
                        sessionManager)
                )
                .build()
            val finalUrl = (networkConfiguration.baseUrl + url)
            val bodyJson = body?.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()

                .url(finalUrl)
                .method(method.uppercase(), when (method.uppercase()) {
                    "POST", "PUT", "PATCH" -> bodyJson
                    else -> null
                })
                .build()
            val response = client.newCall(request).execute()
            Log.d("OrderSync", "Response: ${response.code} - ${response.body?.string()}")
            if (response.isSuccessful) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: IOException) {
            Log.d("OrderSync",e.message?:"")
            Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}



fun enqueueOrderUpdateTask(context: Context, method: String, url: String, body: String) {
    val workRequest = OneTimeWorkRequestBuilder<SyncOrderUpdateWorker>()
        .setInputData(
            workDataOf(
                "method" to method,
                "url" to url,
                "body" to body
            )
        )
        .setConstraints(Constraints.Builder().setRequiredNetworkType(networkType = NetworkType.CONNECTED).build())
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}
