package com.spiraldev.todoapp.ui.todolist

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.spiraldev.todoapp.core.base.BaseViewModel
import com.spiraldev.todoapp.data.FilterOption
import com.spiraldev.todoapp.data.ToDoStatus
import com.spiraldev.todoapp.data.database.ToDoDatabase
import com.spiraldev.todoapp.data.database.ToDoEntity
import com.spiraldev.todoapp.data.prefs.PreferenceStorage
import com.spiraldev.todoapp.util.combineWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

import javax.inject.Inject


class ToDoListViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val db: ToDoDatabase
) :
    BaseViewModel() {

    private val _refreshLD = MutableLiveData<Boolean>(false)
    private val _allToDoLD: LiveData<List<ToDoEntity>> = db.todoDao().allToDoList()

    fun refreshToDoList() {
        _refreshLD.value = true
    }

    val filteredToDoLD = _allToDoLD.combineWith(_refreshLD) { todoList, _ ->
        val fOption = getFilterOption()
        val now = Calendar.getInstance()
        val dateRange = getDateRange()

        todoList?.map { element ->
            element.completionTime?.let {
                if (it < now) {
                    element.status = ToDoStatus.OVERDUE
                }
            }
        }

        todoList?.filter { element ->
            var isInRange = true

            element.completionTime?.let {
                isInRange = (dateRange.first <= it.timeInMillis) &&
                        (it.timeInMillis <= dateRange.second)
            }

            isInRange && (fOption == FilterOption.ALL || FilterOption.valueOf(element.status.name) == fOption)
        }
    }

    @WorkerThread
    fun deleteToDo(todo: ToDoEntity) = viewModelScope.launch(Dispatchers.IO) {
        db.todoDao().deleteToDo(todo)
    }

    @WorkerThread
    fun insert(todo: ToDoEntity) = viewModelScope.launch(Dispatchers.IO) {
        db.todoDao().insert(todo)
    }

    @WorkerThread
    fun toggleCompletion(id: Int, status: ToDoStatus) = viewModelScope.launch(Dispatchers.IO) {
        db.todoDao().toggleCompletion(id, status)
    }

    fun getFilterOption(): FilterOption = preferenceStorage.filterBy

    fun setFilterOption(fOption: FilterOption) {
        preferenceStorage.filterBy = fOption
    }

    fun setDateRange(from: Long, to: Long) {
        preferenceStorage.fromTimestamp = from
        preferenceStorage.toTimestamp = to
    }

    fun getDateRange(): Pair<Long, Long> {
        return Pair(preferenceStorage.fromTimestamp, preferenceStorage.toTimestamp)
    }
}