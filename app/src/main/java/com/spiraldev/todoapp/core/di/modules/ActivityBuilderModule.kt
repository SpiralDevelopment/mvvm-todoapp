package com.spiraldev.todoapp.core.di.modules

import com.spiraldev.todoapp.core.di.annotations.ActivityScope
import com.spiraldev.todoapp.ui.MainActivity
import com.spiraldev.todoapp.ui.addedit.AddEditModule
import com.spiraldev.todoapp.ui.addedit.AddEditViewModel
import com.spiraldev.todoapp.ui.todolist.ToDoListModule
import com.spiraldev.todoapp.ui.todolist.ToDoListViewModel
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [ToDoListModule::class, AddEditModule::class])
    abstract fun MainActivity(): MainActivity
}
