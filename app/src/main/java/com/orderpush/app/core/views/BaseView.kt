@file:OptIn(ExperimentalMaterial3Api::class)

package com.orderpush.app.core.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.orderpush.app.core.router.LocalNavigation

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
    actions:@Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    appBarColors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),

    // Bottom Navigation Configuration
    bottomNavigationItems: List<NavigationItem> = emptyList(),
    selectedBottomNavIndex: Int = 0,

    // Bottom App Bar Configuration
    bottomAppBar: (@Composable () -> Unit)? = null,
    leading:(@Composable () ->Unit )? =null,
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,

    // Content
    content: @Composable  () -> Unit
) {
    val navigator = LocalNavigation.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        topBar = {
            if (title != null) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if(navigator.canPop()){
                            BackButton()
                        }
                        leading?.invoke()
                    },
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(end = 20.dp)
                        ) { actions() }
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

            Box(modifier = Modifier
                .padding(it)
                .fillMaxSize()) {
                //column scope
                    content()

            }
        }
    )
}