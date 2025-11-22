package com.orderpush.app.core.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orderpush.app.FullScreenNotificationActivity
import com.orderpush.app.MainActivity
import com.orderpush.app.R

class FcmMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("fcm", token)
        super.onNewToken(token)
        // Send token to your server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val payload = remoteMessage.data
        val title = remoteMessage.data["title"] ?: "New Message"
        val body = remoteMessage.data["body"] ?: "You have a new notification"

        showFullScreenNotification(title, body)
    }

    private fun showFullScreenNotification(title: String, message: String) {
        val channelId = "orderpush_fullscreen_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Full Screen Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for full screen FCM notifications"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                setBypassDnd(true) // Allow notification to bypass Do Not Disturb
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent for full screen activity
        val fullScreenIntent = Intent(this, FullScreenNotificationActivity::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for normal tap (when not in full screen mode)
        val contentIntent = Intent(this, MainActivity::class.java)
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val contentPendingIntent = PendingIntent.getActivity(
            this, 1, contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build full screen notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenPendingIntent, true) // This is the key for full screen
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true) // Makes it harder to dismiss accidentally
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}