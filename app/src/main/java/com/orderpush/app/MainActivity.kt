package com.orderpush.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.google.firebase.FirebaseApp
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.NavigationViewModel
import com.orderpush.app.core.router.Screen
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.analytics.presentation.view.AnalyticsScreen
import com.orderpush.app.features.auth.presentation.view.LoginScreen
import com.orderpush.app.features.category.presentation.view.CategoryDashboardScreen
import com.orderpush.app.features.dashboard.presentation.view.DashboardSelectionScreen
import com.orderpush.app.features.kds.presentation.view.KdsDashboardScreen
import com.orderpush.app.features.kds.presentation.view.KdsDisplaySettingsScreen
import com.orderpush.app.features.kds.presentation.view.KdsFontAndColorSettingsScreen
import com.orderpush.app.features.kds.presentation.view.KdsGeneralSettingsScreen
import com.orderpush.app.features.kds.presentation.view.KdsSettingStationScreen
import com.orderpush.app.features.kds.presentation.view.KdsSettingsScreen
import com.orderpush.app.features.kds.presentation.view.KdsSoundSettingsScreen
import com.orderpush.app.features.menuItem.presentation.view.MenuItemDashboardScreen
import com.orderpush.app.features.menumanger.presentation.view.MenuManagerDashboardScreen
import com.orderpush.app.features.modifier.presentation.view.ModifierDashboardScreen
import com.orderpush.app.features.order.presentation.view.OrderDetailsScreen
import com.orderpush.app.features.order.presentation.view.OrderDashboardScreen
import com.orderpush.app.features.pos.presentation.view.PosDashboardScreen
import com.orderpush.app.features.printer.presentation.view.PrinterConnectionScreen
import com.orderpush.app.features.printer.presentation.view.PrinterTypeSelectionScreen
import com.orderpush.app.ui.theme.OrderpushAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->

    }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        askNotificationPermission()
        enableEdgeToEdge(
        )
        setContent {
            OrderpushAppTheme(
                darkTheme = true
            ) {
                val navigationViewModel=hiltViewModel<NavigationViewModel>()
                CompositionLocalProvider(
                    LocalNavigation provides navigationViewModel
                ) {
                    val listDetailStrategy = rememberListDetailSceneStrategy<Screen>()
                    NavDisplay(
                        backStack = navigationViewModel.backStack,
                        sceneStrategy = listDetailStrategy,
                        onBack = {
                            navigationViewModel.pop()
                        },
                        entryProvider = entryProvider {
                            entry<Screen.Login>{
                                LoginScreen()
                            }
                            entry<Screen.KdsDashboard> {
                                KdsDashboardScreen()
                            }
                            entry<Screen.KdsSettings>(
                                metadata = ListDetailSceneStrategy.listPane(
                                    detailPlaceholder = {
                                        KdsGeneralSettingsScreen()
                                    }
                                ),

                            ) {

                                KdsSettingsScreen()
                            }
                            entry<Screen.Analytics> {
                                AnalyticsScreen()
                            }
                            entry<Screen.OrderDashboard>(

                            ) {
                                OrderDashboardScreen()
                            }
                            entry<Screen.PrinterConnection>(
                                 metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                PrinterConnectionScreen(it.printerType)
                            }
                            entry<Screen.OrderDetails>(

                            ) {
                                OrderDetailsScreen(it.id,showAsFullPage = true)
                            }
                            entry<Screen.KdsGeneralSettings>(
                                metadata = ListDetailSceneStrategy.detailPane(),
                            ) {
                                KdsGeneralSettingsScreen()
                            }
                            entry<Screen.KdsDisplayModes>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                KdsDisplaySettingsScreen()
                            }

                            entry<Screen.KdsSoundSettings>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                KdsSoundSettingsScreen()
                            }
                            entry<Screen.KdsFontAndColors>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                KdsFontAndColorSettingsScreen()
                            }

                            entry<Screen.KdsScreenCommunication>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                KdsSettingStationScreen()
                            }

                            entry<Screen.PrinterTypeSelectionScreen>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                PrinterTypeSelectionScreen()
                            }

                            entry<Screen.DashboardSelection> {
                                DashboardSelectionScreen()
                            }

                            entry<Screen.PosDashboard> {
                                PosDashboardScreen()
                            }

                            entry<Screen.CategoryDashboard> {
                                CategoryDashboardScreen()
                            }

                            entry<Screen.MenuItemDashboard> {
                                MenuItemDashboardScreen()
                            }

                            entry<Screen.ModifierDashboard> {
                                ModifierDashboardScreen()
                            }
                            entry<Screen.MenuManagerDashboard> {
                                MenuManagerDashboardScreen()
                            }

                        }
                    )
                }
            }
        }
    }
}
