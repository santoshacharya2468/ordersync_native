package com.orderpush.app.features.auth.data.api

import com.orderpush.app.core.network.APIResponse
import com.orderpush.app.core.services.DeviceInfo
import com.orderpush.app.features.auth.data.model.EmailPasswordLoginRequest
import com.orderpush.app.features.auth.data.model.LinkedDevice
import com.orderpush.app.features.auth.data.model.LoginRequest
import com.orderpush.app.features.auth.data.model.LoginResponse
import com.orderpush.app.features.auth.data.model.LogoutDeviceRequest
import com.orderpush.app.features.auth.data.model.RefreshTokenRequest
import com.orderpush.app.features.auth.data.model.UpdateDeviceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface AuthApi {
        @POST("auth/login")
        suspend fun login(@Body request: EmailPasswordLoginRequest): Response<APIResponse<LoginResponse>>
        @POST("devices")
        suspend fun linkDevice(@Body request: DeviceInfo,@Query("store") store:String): Response<APIResponse<LinkedDevice>>

        @PUT("devices/{id}")
        suspend fun  updateDevice(@Body request: UpdateDeviceRequest,
                                  @Path("id")id:String,
                                  @Query("store") store:String

                                  ):Response<APIResponse<LinkedDevice>>

        @GET("devices")
        suspend fun getLinkedDevices(@Query("store")store:String):Response<APIResponse<List<LinkedDevice>>>

        @POST("devices/logout")
        suspend fun  logoutDevice(@Body payload: LogoutDeviceRequest):Response<APIResponse<Unit>>

        @POST("auth/refresh-token")
   suspend fun refreshToken(@Body payload: RefreshTokenRequest):Response<APIResponse<LinkedDevice>>

    }
