package com.orderpush.app.features.auth.data.repository

import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.core.network.isSuccess
import com.orderpush.app.core.services.DeviceInfo
import com.orderpush.app.core.services.getDeviceInfo
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.auth.data.api.AuthApi
import com.orderpush.app.features.auth.data.model.EmailPasswordLoginRequest
import com.orderpush.app.features.auth.data.model.LinkedDevice
import com.orderpush.app.features.auth.data.model.LoginRequest
import com.orderpush.app.features.auth.data.model.LoginResponse
import com.orderpush.app.features.auth.data.model.LogoutDeviceRequest
import com.orderpush.app.features.auth.data.model.UpdateDeviceRequest
import com.orderpush.app.features.auth.domain.repository.AuthRepository
import jakarta.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val context: android.content.Context,
    private  val sessionManager: SessionManager
) : AuthRepository {
   override suspend fun login(email: kotlin.String, password: String): APIResponse<LoginResponse> {
        val deviceInfo=getDeviceInfo(context)
        val response= api.login(EmailPasswordLoginRequest(email=email,password= password,deviceInfo)).toApiResponse()
        return  response
    }

    override suspend fun getLinkedDevices(): APIResponse<List<LinkedDevice>> {
       return api.getLinkedDevices(sessionManager.getStore()?:"").toApiResponse()
    }

    override suspend fun updateDevice(payload: UpdateDeviceRequest): APIResponse<LinkedDevice> {
        return api.updateDevice(request = payload,
           store =  sessionManager.getStore()?:"",
            id = sessionManager.getDeviceId()?:""
            ).toApiResponse()
    }

    override suspend fun logoutDevice():APIResponse<Unit> {
        val deviceInfo=getDeviceInfo(context)
       return api.logoutDevice(payload = LogoutDeviceRequest(deviceId = deviceInfo.id)).toApiResponse()
    }
}