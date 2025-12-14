package com.orderpush.app.features.modifier.domain.repository

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.modifier.data.model.request.ModifierFilter
import com.orderpush.app.features.modifier.data.model.response.MenuModifier

interface ModifierRepository {

    suspend fun  getModifiers(filter: ModifierFilter): APIResponse<List<MenuModifier>>
}