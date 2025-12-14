package com.orderpush.app.features.modifier.data.repository

import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.modifier.data.api.ModifierApi
import com.orderpush.app.features.modifier.data.model.request.ModifierFilter
import com.orderpush.app.features.modifier.data.model.response.MenuModifier
import com.orderpush.app.features.modifier.domain.repository.ModifierRepository
import jakarta.inject.Inject

class ModifierRepositoryImpl @Inject constructor(private  val api: ModifierApi) : ModifierRepository{
    override suspend fun getModifiers(filter: ModifierFilter): APIResponse<List<MenuModifier>> {
        return api.getModifiers().toApiResponse()
    }
}