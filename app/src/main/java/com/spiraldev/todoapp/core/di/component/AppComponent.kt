package com.spiraldev.todoapp.core.di.component

import android.app.Application
import com.spiraldev.todoapp.MyApp
import com.spiraldev.todoapp.core.di.modules.ActivityBuilderModule
import com.spiraldev.todoapp.core.di.modules.AppModule
import com.spiraldev.todoapp.core.di.modules.BroadcastReceiverModule
import com.spiraldev.todoapp.core.di.modules.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        ActivityBuilderModule::class,
        AppModule::class,
        BroadcastReceiverModule::class,
        ViewModelFactoryModule::class]
)
interface AppComponent : AndroidInjector<MyApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
