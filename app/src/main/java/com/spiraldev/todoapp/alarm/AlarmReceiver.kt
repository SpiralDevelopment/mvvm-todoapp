package com.spiraldev.todoapp.alarm

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.WorkerThread
import com.spiraldev.todoapp.data.database.ToDoDatabase
import com.spiraldev.todoapp.data.database.ToDoEntity
import dagger.android.DaggerBroadcastReceiver
import kotlinx.coroutines.*
import javax.inject.Inject


class AlarmReceiver : DaggerBroadcastReceiver() {

    companion object {
        val TODO_ID = "id"
    }

    @Inject
    lateinit var toDoDatabase: ToDoDatabase

    private val alarmScope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        intent?.let {
            val todoId = it.getIntExtra(TODO_ID, -1)

            alarmScope.launch {
                val todo = toDoDatabase.todoDao().getById(todoId)

                context?.let { context ->
                    NotificationHelper.showNotification(
                        context,
                        todo.title,
                        todo.id,
                        todo.description ?: ""
                    )
                }
            }
        }
    }
}