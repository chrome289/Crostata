package xyz.siddharthseth.crostata.di.module.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import xyz.siddharthseth.crostata.di.scope.ui.MainActivityScope
import xyz.siddharthseth.crostata.ui.view.fragment.HomeFragment
import xyz.siddharthseth.crostata.ui.view.fragment.MainFragment
import xyz.siddharthseth.crostata.ui.view.fragment.PostFragment
import xyz.siddharthseth.crostata.ui.view.fragment.ProfileFragment

@Module
abstract class FragmentModule {
    @MainActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainFragmentInjector(): MainFragment


    @MainActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHomeFragmentInjector(): HomeFragment

    @MainActivityScope
    @ContributesAndroidInjector
    abstract fun contributeProfileFragmentInjector(): ProfileFragment


    @MainActivityScope
    @ContributesAndroidInjector
    abstract fun contributePostFragmentInjector(): PostFragment
}