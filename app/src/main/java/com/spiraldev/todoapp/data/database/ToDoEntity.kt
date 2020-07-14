package com.spiraldev.todoapp.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spiraldev.todoapp.data.ToDoStatus
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "todo_list")
data class ToDoEntity(
    var title: String,
    var description: String?,
    var completionTime: Calendar? = null,
    var notifyHourBefore: Int = -1,
    var status: ToDoStatus = ToDoStatus.ACTIVE,
    var imagePath: String?,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable