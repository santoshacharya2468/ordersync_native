package com.orderpush.app.features.menuItem.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.menuItem.presentation.viewmodel.MenuItemUIState
import com.orderpush.app.features.menuItem.presentation.viewmodel.MenuItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDashboardScreen(){
    val viewModel= hiltViewModel<MenuItemViewModel>()
    LaunchedEffect(Unit) {
        viewModel.getMenuItems()

    }
    val state = viewModel.uiState.collectAsState()
    BaseView(title = "MenuItem Manger",
        floatingActionButton = {
            FloatingActionButton(onClick = {

            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add new modifier")
            }
        }
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
                    MenuItemView(it)
                }
            }
        }
    }
}