package xyz.siddharthseth.crostata.di.module.ui

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import xyz.siddharthseth.crostata.di.component.ProfileActivityComponent
import xyz.siddharthseth.crostata.ui.view.activity.ProfileActivity

@Module(subcomponents = [ProfileActivityComponent::class])
abstract class ProfileActivityModule {
    @Binds
    @IntoMap
    @ClassKey(ProfileActivity::class)
    internal abstract fun bindProfileActivityAndroidInjectorFactory(factory: ProfileActivityComponent.Factory): AndroidInjector.Factory<*>
}