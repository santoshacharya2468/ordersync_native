package com.orderpush.app.features.category.presentation.view

import android.widget.GridView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.category.presentation.viewmodel.CategoryUIState
import com.orderpush.app.features.category.presentation.viewmodel.CategoryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDashboardScreen(){
    val viewModel= hiltViewModel<CategoryViewModel>()
    LaunchedEffect(Unit) {
        viewModel.getCategories()

    }
    val state=viewModel.uiState.collectAsState()
    BaseView(title = "Category Manger",
          floatingActionButton = {
              FloatingActionButton(onClick = {

              }) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = "add new modifier")
              }
          }
        ) {

     if(state.value is CategoryUIState.Success) {
         val data = (state.value as CategoryUIState.Success).data

         LazyVerticalGrid(
             columns = GridCells.FixedSize(200.dp),
             contentPadding = PaddingValues(10.dp),
             horizontalArrangement = Arrangement.spacedBy(10.dp),
             verticalArrangement = Arrangement.spacedBy(10.dp),

             ) {
             items(data) {
                 CategoryGridItemView(it)
             }
         }
     }
    }
}