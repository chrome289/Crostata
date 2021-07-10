package xyz.siddharthseth.crostata.di.component

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import xyz.siddharthseth.crostata.Crostata
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.data.repository.local.UserRepository
import xyz.siddharthseth.crostata.data.repository.local.disk.StorageRepository
import xyz.siddharthseth.crostata.di.module.*
import xyz.siddharthseth.crostata.di.module.ui.*
import xyz.siddharthseth.crostata.di.module.usecase.FeedUseCaseModule
import xyz.siddharthseth.crostata.di.scope.AppScope
import xyz.siddharthseth.crostata.util.TimeConverter

@AppScope
@Component(
    modules = [
        (AndroidInjectionModule::class),
        (AndroidSupportInjectionModule::class),

        (MainActivityModule::class),
        (ProfileActivityModule::class),
        (PostActivityModule::class),

        (AppModule::class),
        (NetworkModule::class),
        (ApiModule::class),
        (RepositoryModule::class),
        (DaoModule::class),

        (ActivityModule::class),
        (FragmentModule::class),
        (ViewModelModule::class),

        (FeedUseCaseModule::class)]
)

interface AppComponent : AndroidInjector<Crostata> {
    fun inject(application: Application)

    fun getApplication(): Application
    fun getSharedPreferencesDataStore(): SharedPreferencesDao

    fun getTimeConverter(): TimeConverter

    fun getViewModelFactory(): ViewModelProvider.Factory

    fun getUserRepository(): UserRepository
    fun getStorageRepository(): StorageRepository
    fun getApiManager(): ApiManager
}