package com.orderpush.app.core.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.tasks.await
import java.net.Inet4Address
import java.net.NetworkInterface


data class DeviceInfo(
    val name:String,
    @SerializedName("device_id")
    val id:String ,
    @SerializedName("fcm_token")
    val fcmToken:String,
    val os:String,
    val ip:String,
    val version:String
    )

@SuppressLint("HardwareIds")
suspend fun getDeviceInfo(context: Context): DeviceInfo {
    val name = "${Build.MANUFACTURER} ${Build.MODEL}"
    val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    val fcmToken = FirebaseMessaging.getInstance().token.await()
    val ip= getLocalIpAddress()
    val version= context.getAppVersion()
    val os = "android"
    return DeviceInfo(
        name = name,
        id = id,
        fcmToken = fcmToken,
        os = os,
        ip = ip,
        version = version
    )
}



fun getLocalIpAddress(): String {
    return try {
        NetworkInterface.getNetworkInterfaces()
            .toList()
            .flatMap { it.inetAddresses.toList() }
            .firstOrNull {
                !it.isLoopbackAddress && it is Inet4Address
            }
            ?.hostAddress ?: "Unknown"
    } catch (_: Exception) {
        "Unknown"
    }
}


fun Context.getAppVersion(): String {
    return try {
        val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }

        packageInfo.versionName ?: "Unknown"
    } catch (e: Exception) {
        e.printStackTrace()
        "Unknown"
    }
}