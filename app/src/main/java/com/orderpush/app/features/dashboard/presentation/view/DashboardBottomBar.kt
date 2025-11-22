package com.orderpush.app.features.dashboard.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Sealed class for navigation items
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {

    object Orders : BottomNavItem(
        "orders",
        "Orders",
        Icons.Outlined.Receipt,
        Icons.Filled.Receipt
    )
    object Analytics : BottomNavItem(
        "analytics",
        "Analytics",
        Icons.Outlined.Analytics,
        Icons.Filled.Analytics
    )
    object Prints : BottomNavItem(
        "prints",
        "Prints",
        Icons.Outlined.LocalPrintshop,
        Icons.Filled.LocalPrintshop
    )
    object Settings : BottomNavItem(
        "Settings",
        "Settings",
        Icons.Outlined.Settings,
        Icons.Filled.Settings
    )
}

// Modern Bottom Navigation Bar with dark theme
@Composable
fun DashboardBottomNavBar(
    currentPage: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Orders,
        BottomNavItem.Analytics,
        BottomNavItem.Prints,
        BottomNavItem.Settings
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding( vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),

            color = Color(0xFF20232a),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavBarItem(
                        item = item,
                        isSelected = currentPage == index,
                        onClick = { onItemSelected(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF121212) else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color(0xFF888888)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .height(56.dp),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(24.dp)
            )

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

