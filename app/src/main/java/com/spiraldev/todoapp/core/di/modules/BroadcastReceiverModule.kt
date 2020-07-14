package com.spiraldev.todoapp.core.di.modules

import com.spiraldev.todoapp.alarm.AlarmReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class BroadcastReceiverModule {

    @ContributesAndroidInjector
    abstract fun contributeAlarmReceiver(): AlarmReceiver
}