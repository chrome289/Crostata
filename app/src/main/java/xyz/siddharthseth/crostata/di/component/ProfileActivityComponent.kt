package xyz.siddharthseth.crostata.di.component

import dagger.Subcomponent
import dagger.android.AndroidInjector
import xyz.siddharthseth.crostata.ui.view.activity.ProfileActivity

@Subcomponent
interface ProfileActivityComponent : AndroidInjector<ProfileActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<ProfileActivity>
}