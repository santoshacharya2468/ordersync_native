package com.orderpush.app.features.setting.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.AppButton
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.auth.presentation.viewmodel.AuthState
import com.orderpush.app.features.auth.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen () {
    val navigator= LocalNavigation.current
    @OptIn(ExperimentalMaterial3Api::class)
        val authViewModel = hiltViewModel<AuthViewModel>()
        val authState by authViewModel.loginState.collectAsState()
        LaunchedEffect(authState) {
            if (authState is AuthState.LogoutSuccess) {
                navigator.replaceAll(Screen.Login)
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