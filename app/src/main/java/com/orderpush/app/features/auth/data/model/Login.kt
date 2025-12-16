package com.orderpush.app.features.auth.data.model

import com.orderpush.app.features.store.data.model.Store

data class LoginResponse(val device : LinkedDevice,val user: User, val stores: List<Store>)
data class LoginRequest(val email:String,val password:String)