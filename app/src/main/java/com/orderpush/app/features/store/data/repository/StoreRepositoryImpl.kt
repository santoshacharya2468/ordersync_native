package com.orderpush.app.features.store.data.repository

import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.features.store.data.api.StoreApi
import com.orderpush.app.features.store.domain.repository.StoreRepository
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(private  val api: StoreApi) : StoreRepository {
    override suspend fun getLinkedStores() = api.getLinkedStores().toApiResponse()

}