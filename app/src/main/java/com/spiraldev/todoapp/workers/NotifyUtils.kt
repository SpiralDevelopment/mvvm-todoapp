package com.spiraldev.todoapp.workers

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.spiraldev.todoapp.data.database.ToDoEntity
import java.util.*
import java.util.concurrent.TimeUnit


object NotifyUtils {

    fun cancelNotification(
        context: Context,
        id: Int
    ) {
        WorkManager.getInstance(context).cancelAllWorkByTag(id.toString())
    }

    fun setUpNotification(
        context: Context,
        todo: ToDoEntity
    ) {
        val notifyHoursBefore = todo.notifyHourBefore

        if (notifyHoursBefore == -1) {
            return
        }

        todo.completionTime?.let { c ->
            c.add(Calendar.HOUR_OF_DAY, -notifyHoursBefore)
            val initDelay = c.timeInMillis - System.currentTimeMillis()

            if (initDelay > 0) {
                val data = Data.Builder()
                data.putString(NotifyWorker.TASK_NAME_DATA, todo.title)

                val notifyManager = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                    .setInputData(data.build())
                    .setInitialDelay(
                        initDelay,
                        TimeUnit.MILLISECONDS
                    )
                    .addTag(todo.id.toString())
                    .build()

                WorkManager.getInstance(context).enqueue(notifyManager)
            }

        }
    }
}