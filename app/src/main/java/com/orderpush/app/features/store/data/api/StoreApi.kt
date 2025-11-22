package com.orderpush.app.features.store.data.api

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.store.data.model.Store
import retrofit2.Response
import retrofit2.http.GET

interface StoreApi {
    @GET("stores/linked")
    suspend fun getLinkedStores(): Response<APIResponse<List<Store>>>
}