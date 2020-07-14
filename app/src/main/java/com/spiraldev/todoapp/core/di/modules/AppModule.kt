package com.spiraldev.todoapp.core.di.modules

import android.app.Application
import android.content.Context
import com.spiraldev.todoapp.data.database.ToDoDatabase
import com.spiraldev.todoapp.data.prefs.PreferenceStorage
import com.spiraldev.todoapp.data.prefs.SharedPreferenceStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule{

    @Provides
    @Singleton
    fun provideContext(app : Application) : Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun providePreferenceStorage(context: Context): PreferenceStorage = SharedPreferenceStorage(context)

    @Provides
    @Singleton
    fun provideDatabase(context: Context): ToDoDatabase = ToDoDatabase.buildDatabase(context)
}