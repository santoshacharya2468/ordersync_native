package com.orderpush.app.features.auth.data.model

import com.google.gson.annotations.SerializedName

data class UpdateDeviceRequest(
    @SerializedName("fcm_token")
    val fcmToken:String?=null,
    @SerializedName("station_id")
    val stationId:String?=null,
    @SerializedName("store")
    val store:String?=null
)
