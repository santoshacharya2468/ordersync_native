package com.orderpush.app.features.auth.data.model

data class LinkedDevice(
    val id: String,
    val name: String,
    val deviceId: String,
    val os: String,
    val storeId: String?,
    val userId: String,
    val fcmToken: String,
    val stationId:String?,
    val refreshTokenHash:String?,
    val accessJti:String?
)
