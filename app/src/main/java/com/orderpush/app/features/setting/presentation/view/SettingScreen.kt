package com.orderpush.app.features.setting.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.orderpush.app.core.views.AppButton
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.auth.presentation.view.LoginScreen
import com.orderpush.app.features.auth.presentation.viewmodel.AuthState
import com.orderpush.app.features.auth.presentation.viewmodel.AuthViewModel

class SettingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content()
    {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel = hiltViewModel<AuthViewModel>()
        val authState by authViewModel.loginState.collectAsState()
        LaunchedEffect(authState) {
            if (authState is AuthState.LogoutSuccess) {
                navigator.replaceAll(LoginScreen())
            }
        }
        BaseView(
            title = "Settings"
        ) {
            Column {
                AppButton(
                    text = "logout",
                    onClick = {
                        authViewModel.logout()
                    }
                )
            }
        }
    }
}