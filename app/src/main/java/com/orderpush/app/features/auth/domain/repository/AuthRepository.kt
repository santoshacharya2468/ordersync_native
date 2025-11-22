package com.orderpush.app.features.auth.domain.repository

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.core.services.DeviceInfo
import com.orderpush.app.features.auth.data.model.LinkedDevice
import com.orderpush.app.features.auth.data.model.LoginResponse
import com.orderpush.app.features.auth.data.model.UpdateDeviceRequest

interface AuthRepository {
    suspend fun login(email: kotlin.String, password: String): APIResponse<LoginResponse>
    suspend fun  getLinkedDevices(): APIResponse<List<LinkedDevice>>
    suspend   fun updateDevice(payload: UpdateDeviceRequest): APIResponse<LinkedDevice>
    suspend fun logoutDevice():APIResponse<Unit>

}