package xyz.siddharthseth.crostata.di.module.ui

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import xyz.siddharthseth.crostata.di.component.PostActivityComponent
import xyz.siddharthseth.crostata.ui.view.activity.PostActivity

@Module(subcomponents = [PostActivityComponent::class])
abstract class PostActivityModule {
    @Binds
    @IntoMap
    @ClassKey(PostActivity::class)
    internal abstract fun bindPostActivityAndroidInjectorFactory(factory: PostActivityComponent.Factory): AndroidInjector.Factory<*>
}