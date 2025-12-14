package com.orderpush.app.features.menuItem.data.repository

import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.menuItem.data.api.MenuItemApi
import com.orderpush.app.features.menuItem.data.model.request.MenuItemFilter
import com.orderpush.app.features.menuItem.data.model.response.MenuItem
import com.orderpush.app.features.menuItem.domain.repository.MenuItemRepository
import jakarta.inject.Inject

class MenuItemRepositoryImpl @Inject constructor(private  val apiService: MenuItemApi) : MenuItemRepository{

    override suspend fun getMenuItems(filter: MenuItemFilter): APIResponse<List<MenuItem>> {
        return apiService.getMenuItems().toApiResponse()
    }
}