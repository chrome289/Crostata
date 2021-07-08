package xyz.siddharthseth.crostata.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.siddharthseth.crostata.data.dao.local.database.UserDao
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.data.repository.local.UserRepository
import xyz.siddharthseth.crostata.data.repository.local.disk.StorageRepository
import xyz.siddharthseth.crostata.di.scope.AppScope

@Module
class RepositoryModule {

    @Provides
    @AppScope
    fun providesUserDataRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @AppScope
    fun providesStorageRepository(app: Application): StorageRepository {
        return StorageRepository(app)
    }


    @Provides
    @AppScope
    fun providesSharedPreferencesDataStore(@AppScope context: Context): SharedPreferencesDao {
        return SharedPreferencesDao(context)
    }

}