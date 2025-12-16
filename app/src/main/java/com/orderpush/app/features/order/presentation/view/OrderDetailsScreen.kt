@file:OptIn(ExperimentalMaterial3Api::class)

package com.orderpush.app.features.order.presentation.view
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.order.presentation.viewmodel.OrderDetailsUiState
import com.orderpush.app.features.order.presentation.viewmodel.OrderDetailsViewModel

@Composable
fun OrderDetailsScreen( orderId: String,showAsFullPage: Boolean=false)  {
        val viewModel = hiltViewModel<OrderDetailsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.selectOrder(orderId)
        }

        when (uiState) {
            OrderDetailsUiState.Idle,
            OrderDetailsUiState.Loading -> {}
            is OrderDetailsUiState.Success -> {
                val order = (uiState as OrderDetailsUiState.Success).order
                if(showAsFullPage){
                    BaseView(title = "Order details"){
                        OrderContentView(order)
                    }
                }else{
                    OrderContentView(order)
                }

            }
            is OrderDetailsUiState.Error -> {}
        }

}


