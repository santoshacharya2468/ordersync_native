package com.orderpush.app.features.pos.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.orderpush.app.R
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.menuItem.presentation.viewmodel.MenuItemUIState
import com.orderpush.app.features.menuItem.presentation.viewmodel.MenuItemViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosDashboardScreen(){
    val menuItemViewModel= hiltViewModel<MenuItemViewModel>()
    LaunchedEffect(Unit) {
        menuItemViewModel.getMenuItems()
    }
    val drawerState= rememberDrawerState(DrawerValue.Closed)
    val scope= rememberCoroutineScope()
    val state=menuItemViewModel.uiState.collectAsState()
    val navigator= LocalNavigation.current

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                PosDrawerView()
            }
        ) {
          BaseView(
              title = "Ordersync Pos",
              leading = {
                  IconButton(
                      onClick = {
                          scope.launch {
                              if (drawerState.isClosed) {
                                  drawerState.open()
                              } else {
                                  drawerState.close()
                              }
                          }
                      }
                  ){
                      Icon(imageVector = Icons.Default.Menu, contentDescription = "menu options")
                  }
              },

          )  {
                Row() {

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (state.value is MenuItemUIState.Success) {
                            val data = (state.value as MenuItemUIState.Success).data
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(200.dp),
                                contentPadding = PaddingValues(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)

                            ) {
                                items(data) {
                                    PosMenuTileView(it)
                                }
                            }
                        }
                    }
                    PosCartView()
                }
            }
        }

}