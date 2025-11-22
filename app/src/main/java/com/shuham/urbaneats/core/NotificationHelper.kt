package com.shuham.urbaneats.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shuham.urbaneats.MainActivity
import com.shuham.urbaneats.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "order_updates"
        const val CHANNEL_NAME = "Order Updates"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for your order status"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showOrderNotification(orderId: String, status: String) {
        // Create Intent to open App when clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setContentTitle("Order Update")
            .setContentText("Order ...${orderId.takeLast(4)} is now $status")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Check permission before showing (Simplified for brevity - handle runtime permission in Activity)
        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(orderId.hashCode(), builder.build())
        } catch (e: SecurityException) {
            // Log error or handle missing permission
        }
    }
}