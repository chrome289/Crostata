package xyz.siddharthseth.crostata.di.component

import dagger.Subcomponent
import dagger.android.AndroidInjector
import xyz.siddharthseth.crostata.ui.view.activity.MainActivity

@Subcomponent
interface MainActivityComponent : AndroidInjector<MainActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MainActivity>
}