package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.core.views.LogoutConfirmationDialog
import com.orderpush.app.features.auth.presentation.viewmodel.AuthViewModel

enum class SelectedScreen(val label:String,val icon: ImageVector,val screen: Screen){
    General("General", Icons.Default.Settings,Screen.KdsGeneralSettings),
    DisplayModes("Display Modes", Icons.Default.Tv, Screen.KdsDisplayModes),
    Sounds("Sounds", Icons.AutoMirrored.Filled.VolumeUp, Screen.KdsSoundSettings),
    FontsColors("Fonts & Colors", Icons.Default.Palette, Screen.KdsFontAndColors),
    ScreenCommunication("Screen Communication", Icons.AutoMirrored.Filled.ScreenShare, Screen.KdsScreenCommunication),
    Printers("Printers", Icons.Default.Print, Screen.PrinterTypeSelectionScreen)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KdsSettingsScreen()  {
        var selectedMenu by remember { mutableStateOf(SelectedScreen.General) }
        var showLogoutDialog by remember { mutableStateOf(false) }
        val authViewModel=hiltViewModel<AuthViewModel>()
        val navigator= LocalNavigation.current

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
            actions = {
                IconButton(
                    onClick = {
                        showLogoutDialog=true
                    }
                ) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "logout", )
                }
            }

        ) {
            Column {
                SelectedScreen.entries.forEach {
                    SettingsMenuItem(
                        icon = it.icon,
                        text =it.label,
                        selected = it==selectedMenu,
                        onClick = {
                           selectedMenu= it
                            navigator.push(it.screen)
                        }
                    )
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
