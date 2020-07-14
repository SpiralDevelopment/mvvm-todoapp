package com.spiraldev.todoapp.ui.addedit

import androidx.annotation.WorkerThread
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiraldev.todoapp.core.base.BaseViewModel
import com.spiraldev.todoapp.data.ToDoStatus
import com.spiraldev.todoapp.data.database.ToDoDatabase
import com.spiraldev.todoapp.data.database.ToDoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class AddEditViewModel @Inject constructor(private val db: ToDoDatabase) :
    BaseViewModel() {
    var newToDoLD = MutableLiveData<ToDoEntity?>()

    @WorkerThread
    fun saveToDo(toDo: ToDoEntity) = viewModelScope.launch(Dispatchers.IO) {
        val insertId = db.todoDao().insert(toDo)

        newToDoLD.postValue(toDo.copy(id = insertId.toInt()))
    }
}