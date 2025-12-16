package com.orderpush.app.features.modifier.data.api

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.modifier.data.model.response.MenuModifier
import retrofit2.Response
import retrofit2.http.GET

interface ModifierApi {
    @GET("modifiers")
    suspend fun  getModifiers(): Response<APIResponse<List<MenuModifier>>>
}