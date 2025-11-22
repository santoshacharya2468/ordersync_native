package com.orderpush.app.features.auth.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName

data class LogoutDeviceRequest(
    @SerializedName("device_id")
    val deviceId:String
)