package com.orderpush.app.core.session

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit
import com.google.gson.Gson
import com.orderpush.app.core.database.AppDatabase
import com.orderpush.app.features.printer.presentation.viewmodel.BasePrinter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Singleton

class SessionManager @Inject constructor(
    private val prefs: SharedPreferences,
    private  val gson: Gson,
    private val database: AppDatabase
) {
    private val ACCESSTOKEN = "access_token"
    private  val REFRESHTOKEN = "refresh_token"
    private val STORE ="store"
    private  val DEVICE_ID ="device_id"
    fun saveAccessToken(token: String) {
        prefs.edit { putString(ACCESSTOKEN, token) }
    }
    fun getAccessToken(): String? = prefs.getString(ACCESSTOKEN, null)
    fun saveRefreshToken(token: String) {
        prefs.edit { putString(REFRESHTOKEN, token) }
    }
    fun saveStore(store: String) {
        prefs.edit { putString(STORE, store) }
    }
    fun getStore(): String? = prefs.getString(STORE, null)
    fun setDeviceId(id:String){
        prefs.edit { putString(DEVICE_ID, id) }
    }
    fun getDeviceId(): String? = prefs.getString(DEVICE_ID, null)
    fun getRefreshToken(): String? = prefs.getString(REFRESHTOKEN, null)
    fun clearSession() {
        database.clearAllTables()
        prefs.edit { clear() }

    }
    fun setLastSyncDate(date:String){
        prefs.edit { putString("last_sync", date) }

    }
    fun getLastSyncDate(): String? = prefs.getString("last_sync", null)
    fun saveSelectedPrinter(printer: BasePrinter?){
        if(printer==null) {
            prefs.edit { { remove("selected_printer") } }
        }
        else{
            prefs.edit {
                putString("selected_printer", gson.toJson(printer))
            }
        }
    }
    fun getSelectedPrinter(): BasePrinter? {
        val printerJson = prefs.getString("selected_printer", null)
        if(printerJson!=null) {
            return gson.fromJson<BasePrinter>(printerJson, BasePrinter::class.java)
        }
        return null
    }
}



@Singleton
class SessionEventBus {
    private val _logoutEvents = MutableSharedFlow<Unit>()
    val logoutEvents = _logoutEvents.asSharedFlow()
    suspend fun emitLogout() {
        _logoutEvents.emit(Unit)
    }
}

