package com.orderpush.app.features.customer.data.api

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.customer.data.model.Customer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CustomerApi {
    @GET("customers")
    suspend fun getCustomers(@Query("store") store: String,
                             @QueryMap filters: Map<String, String>): Response<APIResponse<List<Customer>>>

}