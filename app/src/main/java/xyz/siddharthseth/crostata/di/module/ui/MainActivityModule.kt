package xyz.siddharthseth.crostata.di.module.ui

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import xyz.siddharthseth.crostata.di.component.MainActivityComponent
import xyz.siddharthseth.crostata.ui.view.activity.MainActivity

@Module(subcomponents = [MainActivityComponent::class])
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    internal abstract fun bindMainActivityAndroidInjectorFactory(factory: MainActivityComponent.Factory): AndroidInjector.Factory<*>
}