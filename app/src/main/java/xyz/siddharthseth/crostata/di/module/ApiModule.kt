package xyz.siddharthseth.crostata.di.module

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.data.repository.remote.ApiService
import xyz.siddharthseth.crostata.di.scope.AppScope

@Module
class ApiModule {

    @Provides
    @AppScope
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @AppScope
    fun providesApiManager(
        apiService: ApiService,
        sharedPreferencesDao: SharedPreferencesDao
    ): ApiManager {
        return ApiManager(apiService, sharedPreferencesDao)
    }
}