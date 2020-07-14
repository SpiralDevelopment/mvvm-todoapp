package com.spiraldev.todoapp.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*

class DateTimePicker(
    var mYear: Int = -1,
    var mMonth: Int = -1,
    var mDay: Int = -1
) {
    fun showDateTimePickerDialog(context: Context, defCal: Calendar?, call: (Calendar) -> Unit) {
        val currentDateTime: Calendar = defCal ?: Calendar.getInstance()
        val year = currentDateTime.get(Calendar.YEAR)
        val month = currentDateTime.get(Calendar.MONTH)
        val day = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val hour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentDateTime.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMin ->
                val c = Calendar.getInstance()
                c.set(mYear, mMonth, mDay, selectedHour, selectedMin)
                call(c)
            }, hour, minute, false
        )

        val mDatePicker = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                mYear = selectedYear
                mMonth = selectedMonth
                mDay = selectedDay
                mTimePicker.show()
            }, year, month, day
        )

        mDatePicker.show()
    }
}