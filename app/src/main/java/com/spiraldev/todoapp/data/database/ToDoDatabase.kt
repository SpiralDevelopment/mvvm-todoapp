package com.spiraldev.todoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spiraldev.todoapp.util.Const.DATABASE_NAME
import com.spiraldev.todoapp.util.Const.DATABASE_VERSION
import com.spiraldev.todoapp.util.Converters


@Database(entities = [ToDoEntity::class], version = DATABASE_VERSION, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun todoDao(): ToDoDao

    companion object {
        fun buildDatabase(context: Context): ToDoDatabase {
            return Room.databaseBuilder(context, ToDoDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }
}