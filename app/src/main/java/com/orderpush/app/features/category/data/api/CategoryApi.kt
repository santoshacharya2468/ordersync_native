package com.orderpush.app.features.category.data.api

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.category.data.model.response.Category
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApi {
    @GET("categories")
    suspend fun  getCategory(): Response<APIResponse<List<Category>>>
}