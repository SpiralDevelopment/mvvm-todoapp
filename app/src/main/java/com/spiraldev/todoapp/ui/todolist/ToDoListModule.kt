package com.spiraldev.todoapp.ui.todolist

import androidx.lifecycle.ViewModel
import com.spiraldev.todoapp.core.di.annotations.FragmentScope
import com.spiraldev.todoapp.core.di.annotations.ViewModelKey
import com.spiraldev.todoapp.ui.addedit.AddEditFragment
import com.spiraldev.todoapp.ui.addedit.AddEditViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


@Module
abstract class ToDoListModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeToDoListFragment(): ToDoListFragment

    @Binds
    @IntoMap
    @ViewModelKey(ToDoListViewModel::class)
    abstract fun bindToDoListViewModel(viewModel: ToDoListViewModel): ViewModel
}