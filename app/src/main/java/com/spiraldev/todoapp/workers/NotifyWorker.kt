package com.spiraldev.todoapp.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.spiraldev.todoapp.R
import com.spiraldev.todoapp.ui.MainActivity


class NotifyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    companion object {
        val CHANNEL_ID = "369"
        val TASK_NAME_DATA = "com.spiraldev.todoapp.TASK_NAME_DATA"
    }

    private val task: String? by lazy {
        inputData.getString(TASK_NAME_DATA)
    }

    private val intent: Intent by lazy {
        Intent(applicationContext, MainActivity::class.java)
    }

    private val pi: PendingIntent by lazy {
        PendingIntent.getActivity(
            applicationContext,
            333,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notification: Notification by lazy {
        NotificationCompat.Builder(applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(Color.rgb(30, 136, 229))
            .setContentTitle("Reminder")
            .setContentText(task)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
    }

    override fun doWork(): Result {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val notificationChannel =
                    NotificationChannel(
                        CHANNEL_ID,
                        "Default Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                notificationManager.createNotificationChannel(notificationChannel)
            }

            notificationManager.notify(1112, notification)
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}