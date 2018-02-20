package xyz.siddharthseth.crostata.data.service

import android.util.Log
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import rx.Observable
import xyz.siddharthseth.crostata.data.model.CheckToken
import xyz.siddharthseth.crostata.data.model.Token

interface CrostataApiService {

    @POST("/api/auth/login")
    @FormUrlEncoded
    fun signIn(
            @Field("birth_id") birthId: String,
            @Field("password") password: String
    ): Observable<Token>

    @POST("/api/auth/loginToken")
    fun signInSilently(
            @Header("authorization") token: String
    ): Observable<CheckToken>

    companion object {
        fun Create(): CrostataApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.1.123:3000")
                    .build()

            return retrofit.create(CrostataApiService::class.java)
        }
    }
}