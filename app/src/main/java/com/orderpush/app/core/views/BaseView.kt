@file:OptIn(ExperimentalMaterial3Api::class)

package com.orderpush.app.core.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// Data classes for configuration
data class AppBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)

data class NavigationItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val label: String,
    val onClick: () -> Unit
)

data class FabConfig(
    val icon: ImageVector? = null,
    val text: String? = null,
    val onClick: () -> Unit,
    val extended: Boolean = false
)

enum class AppBarType {
    Small, Medium, Large, CenterAligned
}

@Composable
fun BaseView(
    modifier: Modifier = Modifier,
    // AppBar Configuration
    title: String? = null,
    appBarType: AppBarType = AppBarType.Small,
    actions: List<AppBarAction> = emptyList(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    appBarColors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),

    // FAB Configuration
    fab: FabConfig? = null,

    // Bottom Navigation Configuration
    bottomNavigationItems: List<NavigationItem> = emptyList(),
    selectedBottomNavIndex: Int = 0,

    // Bottom App Bar Configuration
    bottomAppBar: (@Composable () -> Unit)? = null,
    leading:(@Composable () ->Unit )? =null,

    // Content
    content: @Composable  () -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow
    Scaffold(
        modifier = modifier.fillMaxSize(),

        topBar = {
            if (title != null) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if(navigator.items.size >1){
                            BackButton()
                        }
                        leading?.invoke()
                    },
                    actions = {
                        actions.forEach { action ->
                            IconButton(onClick = action.onClick) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = action.contentDescription
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = appBarColors
                )
            }
        },
        bottomBar = {
            when {
                bottomNavigationItems.isNotEmpty() -> {
                    NavigationBar {
                        bottomNavigationItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = index == selectedBottomNavIndex,
                                onClick = item.onClick,
                                icon = {
                                    Icon(
                                        imageVector = if (index == selectedBottomNavIndex) {
                                            item.selectedIcon
                                        } else {
                                            item.icon
                                        },
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
                bottomAppBar != null -> {
                    bottomAppBar()
                }
            }
        },

        content = {

            Box(modifier = Modifier.padding(it).fillMaxSize()) {
                //column scope
                    content()

            }
        }
    )
}