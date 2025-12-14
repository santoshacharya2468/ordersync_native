@file:OptIn(ExperimentalMaterial3Api::class)

package com.orderpush.app.features.order.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.order.data.model.*
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


