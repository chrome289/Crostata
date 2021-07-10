package xyz.siddharthseth.crostata.di.module.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import xyz.siddharthseth.crostata.di.scope.ViewModelKey
import xyz.siddharthseth.crostata.ui.viewmodel.activity.MainActivityViewModel
import xyz.siddharthseth.crostata.ui.viewmodel.activity.PostActivityViewModel
import xyz.siddharthseth.crostata.ui.viewmodel.activity.ProfileActivityViewModel
import xyz.siddharthseth.crostata.ui.viewmodel.fragment.HomeFragmentViewModel
import xyz.siddharthseth.crostata.util.CustomViewModelFactory


@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: CustomViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun mainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeFragmentViewModel::class)
    internal abstract fun homeFragmentViewModel(viewModel: HomeFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileActivityViewModel::class)
    internal abstract fun profileActivityViewModel(viewModel: ProfileActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PostActivityViewModel::class)
    internal abstract fun postFragmentViewModel(viewModel: PostActivityViewModel): ViewModel

}