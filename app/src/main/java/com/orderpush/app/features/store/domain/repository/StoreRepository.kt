package com.orderpush.app.features.store.domain.repository

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.store.data.model.Store

interface StoreRepository {

    suspend fun getLinkedStores(): APIResponse<List<Store>>



}