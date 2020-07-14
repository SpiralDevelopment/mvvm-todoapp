package com.spiraldev.todoapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import com.spiraldev.todoapp.alarm.AlarmReceiver.Companion.TODO_ID

object AlarmHelper{

    fun setReminder(context: Context, wakeUpTime: Long, todoId: Int) {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent =
            createAlarmPendingIntent(
                context,
                todoId,
                todoId
            )

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->
                alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(wakeUpTime, pendingIntent), pendingIntent)
            else -> alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
        }
    }

    private fun createAlarmPendingIntent(context: Context, requestCode: Int, todoId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(TODO_ID, todoId)

        return PendingIntent.getBroadcast(context, requestCode, intent, FLAG_UPDATE_CURRENT)
    }

    fun cancelReminder(context: Context, todoId: Int) {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            createAlarmPendingIntent(
                context,
                todoId,
                todoId
            )

        alarmManager.cancel(pendingIntent)
    }
}