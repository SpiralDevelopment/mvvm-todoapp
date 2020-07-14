package com.spiraldev.todoapp.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.spiraldev.todoapp.data.FilterOption
import com.spiraldev.todoapp.data.ToDoStatus
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


interface PreferenceStorage {
    var filterBy: FilterOption
    var fromTimestamp: Long
    var toTimestamp: Long
}

@Singleton
class SharedPreferenceStorage @Inject constructor(context: Context) : PreferenceStorage {

    companion object {
        const val PREFS_NAME = "com.spiraldev.todoapp"
        const val PREFS_FILTER = "prefs_filter_status"
        const val PREFS_FROM_TIMESTAMP = "prefs_from_timestamp"
        const val PREFS_TO_TIMESTAMP = "prefs_to_timestamp"
    }

    private val prefs: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override var filterBy by FilterPreference(prefs, PREFS_FILTER, FilterOption.ALL)
    override var fromTimestamp by DateRangePreference(
        prefs,
        PREFS_FROM_TIMESTAMP,
        Calendar.getInstance().timeInMillis
    )

    override var toTimestamp by DateRangePreference(
        prefs,
        PREFS_TO_TIMESTAMP,
        Calendar.getInstance().timeInMillis
    )
}

class FilterPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: FilterOption
) : ReadWriteProperty<Any, FilterOption> {

    override fun getValue(thisRef: Any, property: KProperty<*>): FilterOption {
        val filterStr = preferences.value.getString(name, defaultValue.name) ?: defaultValue.name
        return FilterOption.valueOf(filterStr)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: FilterOption) {
        preferences.value.edit { putString(name, value.name) }
    }
}

class DateRangePreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.value.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.value.edit { putLong(name, value) }
    }
}