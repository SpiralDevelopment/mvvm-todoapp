package com.spiraldev.todoapp.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.spiraldev.todoapp.R

object NotificationHelper {
    val NOTIFICATION_CHANNEL_ID = "10001"

    fun showNotification(context: Context, title: String, requestCode: Int, content: String) {
        val ringtone: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationCompat: NotificationCompat.Builder = NotificationCompat.Builder(context)

        val ntf: Notification? = notificationCompat.setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSound(ringtone)
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        val ntfManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            notificationCompat.setChannelId(NOTIFICATION_CHANNEL_ID)
            ntfManager.createNotificationChannel(notificationChannel)
        }

        ntfManager.notify(requestCode, ntf)
    }
}