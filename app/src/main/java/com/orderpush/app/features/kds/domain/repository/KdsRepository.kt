package com.orderpush.app.features.kds.domain.repository

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.kds.data.model.Station
import kotlinx.coroutines.flow.Flow

interface KdsRepository {
    fun getKdsSettings():Flow<KdsSettings?>
   suspend fun  saveKdsSettings(settings: KdsSettings)
    suspend fun  getStations(): APIResponse<List<Station>>
}