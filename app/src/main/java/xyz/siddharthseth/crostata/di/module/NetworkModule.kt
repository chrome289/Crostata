package xyz.siddharthseth.crostata.di.module

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.di.scope.AppScope

@Module
class NetworkModule {


    @Provides
    @AppScope
    fun providesRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @AppScope
    fun provideHttpCache(application: Application): Cache {
        val cacheSize: Long = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize)
    }

    @Provides
    @AppScope
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val client = OkHttpClient.Builder()
            //.addInterceptor(tokenInterceptor)
            .cache(cache)
        return client.build()
    }

    @Provides
    @AppScope
    fun providesRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    @AppScope
    fun providesBaseUrl(application: Application): String {
        return application.resources.getString(R.string.baseurl)
    }
}