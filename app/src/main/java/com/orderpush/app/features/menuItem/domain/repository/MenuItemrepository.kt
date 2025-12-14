package com.orderpush.app.features.menuItem.domain.repository

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.menuItem.data.model.request.MenuItemFilter
import com.orderpush.app.features.menuItem.data.model.response.MenuItem

interface MenuItemRepository {

     suspend fun  getMenuItems(filter: MenuItemFilter): APIResponse<List<MenuItem>>
}