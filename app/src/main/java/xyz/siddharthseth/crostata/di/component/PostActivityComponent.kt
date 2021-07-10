package xyz.siddharthseth.crostata.di.component

import dagger.Subcomponent
import dagger.android.AndroidInjector
import xyz.siddharthseth.crostata.ui.view.activity.PostActivity

@Subcomponent
interface PostActivityComponent : AndroidInjector<PostActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<PostActivity>
}