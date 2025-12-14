package com.orderpush.app.features.menuItem.data.api

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.menuItem.data.model.response.MenuItem
import retrofit2.Response
import retrofit2.http.GET


interface MenuItemApi {

    @GET("menu-items")
    suspend fun  getMenuItems(): Response<APIResponse<List<MenuItem>>>


}