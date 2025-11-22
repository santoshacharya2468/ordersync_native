package com.orderpush.app.features.customer.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.customer.data.model.CustomerFilter
import com.orderpush.app.features.customer.presentation.viewModel.CustomerUIState
import com.orderpush.app.features.customer.presentation.viewModel.CustomerViewModel

class CustomersScreen: Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = hiltViewModel<CustomerViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.getCustomers(CustomerFilter(page = null, limit = null))
        }
       BaseView(
           title = "My Customers"
       ) {   Column {

            when (uiState) {
                is CustomerUIState.Idle -> {}
                is CustomerUIState.Success -> {
                    val customers = (uiState as CustomerUIState.Success).customers
                    LazyColumn {
                        items(customers) {
                            CustomerTileView(customer = it)
                        }
                    }
                }

                is CustomerUIState.Error -> {}
                is CustomerUIState.Loading -> {}
            }
        }
    }
    }

}