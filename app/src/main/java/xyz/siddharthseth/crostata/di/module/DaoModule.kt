package xyz.siddharthseth.crostata.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.siddharthseth.crostata.data.dao.local.database.UserDao
import xyz.siddharthseth.crostata.data.repository.local.database.AppDatabase
import xyz.siddharthseth.crostata.di.scope.AppScope

@Module
class DaoModule {
    @Provides
    @AppScope
    fun providesUserDao(context: Context): UserDao {
        return AppDatabase.getInstance(context).userDao()
    }
}