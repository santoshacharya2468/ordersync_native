package com.orderpush.app.features.auth.data.model


enum  class UserRole{
    admin,vendor
}
data class User(val id:String,val fullName:String,val email:String,val status:String,val role:UserRole)
