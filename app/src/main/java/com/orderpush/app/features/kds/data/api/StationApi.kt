package com.orderpush.app.features.kds.data.api

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.kds.data.model.Station
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StationApi {
    @GET("stations")
    suspend fun  getStation(@Query("store") store:String): Response<APIResponse<List<Station>>>
}