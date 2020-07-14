package com.spiraldev.todoapp.ui.addedit

import androidx.lifecycle.ViewModel
import com.spiraldev.todoapp.core.di.annotations.FragmentScope
import com.spiraldev.todoapp.core.di.annotations.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


@Module
abstract class AddEditModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeAddEditFragment(): AddEditFragment

    @Binds
    @IntoMap
    @ViewModelKey(AddEditViewModel::class)
    abstract fun bindAddEditViewModel(viewModel: AddEditViewModel): ViewModel
}