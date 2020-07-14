package com.spiraldev.todoapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.spiraldev.todoapp.data.ToDoStatus
import java.util.*


@Dao
interface ToDoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: ToDoEntity): Long

    @Query("SELECT * from todo_list WHERE id=:id")
    fun getById(id: Int): ToDoEntity

//    @Query("UPDATE todo_list SET description=:desc, title=:title WHERE id=:id")
//    fun update(id: Int, title: String, desc: String)

//    @Query("UPDATE todo_list SET notifyHourBefore=:notifyHourBefore WHERE id=:id")
//    fun updateNotification(id: Int, notifyHourBefore: Int)

    @Query("Select * from todo_list")
    fun allToDoList(): LiveData<List<ToDoEntity>>

//    @Query("Select * from todo_list WHERE status=1")
//    fun doneToDoList(): LiveData<List<ToDoEntity>>
//
//    @Query("Select * from todo_list WHERE status=0")
//    fun activeToDoList(): LiveData<List<ToDoEntity>>
//
//    @Query("Select * from todo_list WHERE completionTime<:now")
//    fun overdueToDoList(now: Long = Calendar.getInstance().timeInMillis): LiveData<List<ToDoEntity>>

    @Delete
    fun deleteToDo(todo: ToDoEntity)

    @Query("UPDATE todo_list SET status = :newStatus WHERE id=:id")
    fun toggleCompletion(id: Int, newStatus: ToDoStatus)
}