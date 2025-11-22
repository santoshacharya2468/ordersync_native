package com.orderpush.app.features.auth.data.model

import com.orderpush.app.core.services.DeviceInfo

data class EmailPasswordLoginRequest(
    val email:String,
    val password:String,
    val device: DeviceInfo
)