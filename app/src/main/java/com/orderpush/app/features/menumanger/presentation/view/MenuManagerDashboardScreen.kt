package com.orderpush.app.features.menumanger.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.BaseView

enum class MenuType(val screen: Screen, val icon: @Composable () -> Unit) {
    Category(Screen.CategoryDashboard, { Icon(Icons.Default.GridView, null) }),
    MenuItem(Screen.MenuItemDashboard, { Icon(Icons.Default.RestaurantMenu, null) }),
    MenuModifier(Screen.ModifierDashboard, { Icon(Icons.Default.Tune, null) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagerDashboardScreen(){
    val navigator = LocalNavigation.current
    BaseView(
        title = "Menu manager",
        actions = {
            IconButton(onClick = {
                    navigator.push(Screen.DashboardSelection)
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "back")
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.FixedSize(200.dp),
             contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ){
            items(MenuType.entries){
                    MenuTypeButton(it)
            }
        }
    }

}

@Composable
fun MenuTypeButton(type: MenuType){
    val navigator = LocalNavigation.current
    Card(
        onClick = {
            navigator.push(type.screen)
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                type.icon()
            }
            Text(
                text = type.name,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}