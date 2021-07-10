package xyz.siddharthseth.crostata.di.module.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.siddharthseth.crostata.di.scope.ui.MainActivityScope
import xyz.siddharthseth.crostata.ui.view.fragment.HomeFragment
import xyz.siddharthseth.crostata.ui.view.fragment.MainFragment

@Module
abstract class FragmentModule {
    @MainActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainFragmentInjector(): MainFragment


    @MainActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHomeFragmentInjector(): HomeFragment
}