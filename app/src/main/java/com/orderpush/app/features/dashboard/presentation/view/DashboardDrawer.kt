package com.orderpush.app.features.dashboard.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardDrawer(

    drawerState: DrawerState,
    content: @Composable () -> Unit,
) {


    val menuItems = listOf(
        "Menu Items" to Icons.Default.List,
        "Modifiers" to Icons.Default.LocalPizza,
        "Stations" to Icons.Default.Grade,
        "Prep Time" to Icons.Default.AccessTime,
        "Integrations" to Icons.Default.AccountTree,
        "KDS Mode" to Icons.Default.SoupKitchen,
        "Rider  Mode" to Icons.Default.DeliveryDining
    )
    val navigator= LocalNavigation.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(260.dp)
                    .background(MaterialTheme.colorScheme.surface),
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                       Row(
                           modifier = Modifier.padding(horizontal = 8.dp)
                       ) {
                           Box(
                               modifier = Modifier.size(40.dp).background(Color.White,RoundedCornerShape(20.dp))
                           ){

                           }
                           Spacer(modifier = Modifier.width(8.dp))
                           Column {
                               Text("Foodmandu")
                               Text("foodmand@gmail.com", style = MaterialTheme.typography.titleSmall)
                           }
                       }

                        menuItems.forEach { (title, icon) ->
                            NavigationDrawerItem(
                                selected = false,
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title
                                    )
                                },
                                label = { Text(title) },

                                onClick = {
                                    navigator.push(Screen.KdsDashboard)

                                },
                                modifier = Modifier
                                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }

                }
            }
        }
    ) {
        content()
    }
}


