package com.orderpush.app.features.kds.data.repository
import com.orderpush.app.core.database.KdsDao
import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.kds.data.api.StationApi
import com.orderpush.app.features.kds.data.model.KdsSettings
import com.orderpush.app.features.kds.data.model.Station
import com.orderpush.app.features.kds.domain.repository.KdsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow


class KdsRepositoryImpl @Inject constructor(private val kdsDao: KdsDao,
     private  val stationApi: StationApi,
    private  val sessionManager: SessionManager
    ) : KdsRepository  {

    override fun getKdsSettings():Flow<KdsSettings?>{
        return kdsDao.getKdsSettings()
    }
    override suspend fun saveKdsSettings(settings: KdsSettings) {
        kdsDao.saveKdsSettings(settings)
    }
    override suspend fun getStations(): APIResponse<List<Station>> {
        return stationApi.getStation(sessionManager.getStore()?:"").toApiResponse()

    }
}