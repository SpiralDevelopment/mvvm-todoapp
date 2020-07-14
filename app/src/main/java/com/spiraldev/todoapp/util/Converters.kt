package com.spiraldev.todoapp.util

import androidx.room.TypeConverter
import com.spiraldev.todoapp.data.ToDoStatus
import java.util.*

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Calendar? = value?.let {
        GregorianCalendar().also { calendar ->
            calendar.timeInMillis = it
        }
    }

    @TypeConverter
    @JvmStatic
    fun toTimestamp(timestamp: Calendar?): Long? = timestamp?.timeInMillis

    @TypeConverter
    @JvmStatic
    fun toToDoStatus(value: Boolean): ToDoStatus = if (value) ToDoStatus.DONE else ToDoStatus.ACTIVE

    @TypeConverter
    @JvmStatic
    fun fromToDoStatus(value: ToDoStatus): Boolean = value == ToDoStatus.DONE
}