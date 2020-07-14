package com.spiraldev.todoapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    private val FORMATTER = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)

    fun calendarToString(c: Calendar?): String? {
        return c?.let {
            FORMATTER.format(it.time)
        }
    }

    fun stringToCalendar(calendarDate: String): Calendar? {
        return FORMATTER.parse(calendarDate)?.let {
            val cal = Calendar.getInstance()
            cal.time = it
            cal
        }
    }
}