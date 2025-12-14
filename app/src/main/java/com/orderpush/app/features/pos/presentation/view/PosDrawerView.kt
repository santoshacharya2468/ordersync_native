package com.orderpush.app.features.pos.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.LogoutConfirmationDialog
import com.orderpush.app.features.auth.presentation.viewmodel.AuthViewModel

@Composable
fun PosDrawerView(
    onOrdersClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val navigator= LocalNavigation.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    val authViewModel: AuthViewModel = hiltViewModel()
    if(showLogoutDialog) {
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

    Column(
        modifier = Modifier

            .width(300.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)

    ) {
        // Header with teal background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(60.dp)
                        .background(
                            MaterialTheme.colorScheme.onPrimary,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "POS System",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Dough & Crust",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Menu Items
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            DrawerMenuItem(
                icon = Icons.Filled.ShoppingCart,
                label = "Orders",
                isSelected = true,
                onClick = onOrdersClick
            )
            DrawerMenuItem(
                icon = Icons.Filled.Restaurant,
                label = "Menu Items",
                onClick = {
                    navigator.push(Screen.MenuItemDashboard)
                }
            )
            DrawerMenuItem(
                icon = Icons.Filled.Tune,
                label = "Modifiers",
                onClick = {
                    navigator.push(Screen.ModifierDashboard)
                }
            )
            DrawerMenuItem(
                icon = Icons.Filled.GridView,
                label = "Categories",
                onClick = {
                    navigator.push(Screen.CategoryDashboard)
                }
            )
            DrawerMenuItem(
                icon = Icons.Filled.Settings,
                label = "Settings",
                onClick = onSettingsClick
            )
            DrawerMenuItem(
                icon = Icons.Outlined.SwapHoriz,
                label = "Switch Mode",
                onClick = {
                    navigator.push(Screen.DashboardSelection)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout
        DrawerMenuItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            label = "Logout",
            isLogout = true,
            onClick = {
                showLogoutDialog=true
            }
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    isLogout: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isLogout) MaterialTheme.colorScheme.error
            else if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(24.dp)

        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (isLogout) MaterialTheme.colorScheme.error
            else if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}