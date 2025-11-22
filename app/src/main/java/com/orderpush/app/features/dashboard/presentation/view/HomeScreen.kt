package com.orderpush.app.features.dashboard.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.features.analytics.presentation.view.AnalyticsScreen
import com.orderpush.app.features.customer.presentation.view.CustomersScreen
import com.orderpush.app.features.order.presentation.view.OrderListingScreen
import com.orderpush.app.features.setting.presentation.view.SettingScreen

@OptIn(ExperimentalMaterial3Api::class)
class HomeScreen: Screen {
    @Composable
    override fun Content() {
        var currentPage by remember { mutableIntStateOf(0) }
        val pages =listOf<Screen>(
            OrderListingScreen(),
            AnalyticsScreen(),
            AnalyticsScreen(),
            SettingScreen()
        )
        Scaffold (
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                DashboardBottomNavBar(
                   currentPage,
                    onItemSelected = {
                        currentPage=it
                    }
                )
            }
        ) {paddingValues ->
            paddingValues.let { Unit }
           pages[currentPage].Content()
        }
    }
}