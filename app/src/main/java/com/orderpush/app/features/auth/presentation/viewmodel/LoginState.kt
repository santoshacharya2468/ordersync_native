package com.orderpush.app.features.auth.presentation.viewmodel

import com.orderpush.app.features.auth.data.model.User

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    object LogoutSuccess: AuthState()
}