package com.orderpush.app.core.router

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.orderpush.app.core.session.SessionEventBus
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.dashboard.presentation.view.toScreen
import com.orderpush.app.features.printer.presentation.viewmodel.PrinterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject


sealed interface Screen : NavKey {
    @Serializable
    data object  Login: Screen
    @Serializable
    data object Dashboard : Screen
    @Serializable
    data object KdsDashboard : Screen
    @Serializable
    data object KdsSettings: Screen
    @Serializable
    data class  PrinterConnection(val printerType: PrinterType): Screen
    @Serializable
    data object Analytics: Screen
    @Serializable
    data class OrderDetails(val id:String): Screen
    @Serializable
     data object KdsGeneralSettings: Screen
    @Serializable
     data object KdsDisplayModes: Screen
    @Serializable
     data object  KdsSoundSettings: Screen
    @Serializable
    data object  KdsFontAndColors: Screen
    @Serializable
    data object KdsScreenCommunication: Screen
    @Serializable
    data object  KdsPrinterSettings: Screen

    @Serializable
    data object  PrinterTypeSelectionScreen:Screen


    @Serializable
    data object  DashboardSelection: Screen

}

@HiltViewModel
class NavigationViewModel @Inject constructor (  val  sessionManager: SessionManager,
     val eventBus: SessionEventBus
    ): ViewModel(){
    val backStack= mutableStateListOf<Screen>()
    init {
        val nextScreen=(sessionManager.getDashboard())?.toScreen()?: Screen.DashboardSelection
        backStack.add(sessionManager.getAccessToken()?.let { nextScreen } ?: Screen.Login)
        viewModelScope.launch {
            eventBus.logoutEvents.collect {
                replaceAll(Screen.Login)
            }
        }
    }


    fun push(screen: Screen){
        backStack.add(screen)
    }
    fun pop(){
        if (canPop()) backStack.removeLastOrNull()

    }
    fun replace(screen: Screen) {
        if (backStack.isNotEmpty()) {
            backStack.removeLastOrNull()
        }
        backStack.add(screen)
    }

    fun replaceAll(screen: Screen){
        backStack.clear()
        backStack.add(screen)

    }
    fun pushAndReplaceUntil(screen: Screen, match: (Screen) -> Boolean) {
        val index = backStack.indexOfFirst(match)
        if (index >= 0) {
            backStack.subList(index + 1, backStack.size).clear()
        } else {
            backStack.clear()
        }
        backStack.add(screen)
    }

    fun canPop(): Boolean{
        return backStack.size > 1
    }
}


val LocalNavigation = staticCompositionLocalOf<NavigationViewModel> {
    error("NavigationViewModel not provided")
}