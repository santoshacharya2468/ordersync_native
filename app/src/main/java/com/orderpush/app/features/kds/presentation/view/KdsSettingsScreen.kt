package com.orderpush.app.features.kds.presentation.view

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.orderpush.app.core.views.AppBarAction
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.core.views.LogoutConfirmationDialog
import com.orderpush.app.features.auth.presentation.view.LoginScreen
import com.orderpush.app.features.auth.presentation.viewmodel.AuthState
import com.orderpush.app.features.auth.presentation.viewmodel.AuthViewModel
import com.orderpush.app.features.kds.presentation.viewmodel.KdsViewModel
import com.orderpush.app.features.printer.presentation.view.PrinterConnectionView

enum class SelectedScreen(val label:String,val icon: ImageVector){
    General("General", Icons.Default.Settings),
    DisplayModes("Display Modes", Icons.Default.Tv),
    Sounds("Sounds", Icons.AutoMirrored.Filled.VolumeUp),
   // TransitionTimes("Transition Times", Icons.Default.Timer),
   // Orders("Orders", Icons.Default.Receipt),
    FontsColors("Fonts & Colors", Icons.Default.Palette),
    ScreenCommunication("Screen Communication", Icons.AutoMirrored.Filled.ScreenShare),
    Printers("Printers", Icons.Default.Print)
}

class KdsSettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        var selectedMenu by remember { mutableStateOf(SelectedScreen.General) }
        val kdsViewModel= hiltViewModel<KdsViewModel>()
        val settings= kdsViewModel.kdsSettings.collectAsState()
        var showLogoutDialog by remember { mutableStateOf(false) }
        val authViewModel=hiltViewModel<AuthViewModel>()
        val navigator= LocalNavigator.currentOrThrow
        val context= LocalContext.current
        val authState=authViewModel.loginState.collectAsState()
        LaunchedEffect(authState.value) {
            if(authState.value is AuthState.LogoutSuccess){
                Toast.makeText(context,"Logged out", Toast.LENGTH_LONG).show()
                    navigator.replaceAll(LoginScreen())
            }else if(authState.value is AuthState.Error){
                Toast.makeText(context,(authState.value as AuthState.Error).message, Toast.LENGTH_LONG).show()
            }
        }
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    showLogoutDialog = false
                    authViewModel.logout()
                },
                onDismiss = {
                    showLogoutDialog = false
                }
            )
        }
        BaseView(
            title = "Settings",
            actions = listOf(AppBarAction(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = {
                    showLogoutDialog = true
                },
                contentDescription = "logout"
            ))
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Sidebar
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .widthIn(max = 300.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)

                ) {

                    SelectedScreen.entries.forEach {
                        SettingsMenuItem(
                            icon = it.icon,
                            text =it.label,
                            selected = it==selectedMenu,
                            onClick = {
                                selectedMenu=it
                            }
                        )
                    }


                }

                AnimatedContent(selectedMenu){ state->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(8.dp)

                    ) {
                        when (state) {
                            SelectedScreen.General -> KdsGeneralSettingsView(settings.value)
                            SelectedScreen.DisplayModes -> KdsDisplaySettingsView(settings.value)
                            SelectedScreen.Sounds -> KdsSoundSettingsView(settings.value)
                         //   SelectedScreen.TransitionTimes -> KdsTransitionTimeSettingsView(settings.value)
                           // SelectedScreen.Orders -> KdsOrderSettingsView(settings.value)
                            SelectedScreen.FontsColors -> KdsFontAndColorSettingsView(settings.value)
                            SelectedScreen.ScreenCommunication -> KdsSettingStationView(settings.value)
                            SelectedScreen.Printers -> {
                                PrinterConnectionView()
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            color =if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
