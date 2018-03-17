package xyz.siddharthseth.crostata.data.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.NextPosts
import xyz.siddharthseth.crostata.data.model.retrofit.Token
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal

interface CrostataApiService {

    @POST("/api/auth/login")
    @FormUrlEncoded
    fun signIn(
            @Field("birthId") birthId: String,
            @Field("password") password: String
    ): Observable<Response<Token>>

    @POST("/api/auth/loginToken")
    fun signInSilently(
            @Header("authorization") token: String
    ): Observable<Response<ResponseBody>>

    @GET("/api/content/nextPosts")
    fun nextPosts(
            @Header("authorization") token: String,
            @Query("noOfPosts") noOfPosts: Int,
            @Query("lastTimestamp") lastTimestamp: Float,
            @Query("birthId") birthId: String
    ): Observable<NextPosts>

    @POST("/api/opinion/vote")
    @FormUrlEncoded
    fun submitVote(
            @Header("authorization") token: String,
            @Field("postId") postId: String,
            @Field("birthId") birthId: String,
            @Field("value") value: Int
    ): Observable<VoteTotal>

    @DELETE("/api/opinion/vote")
    fun clearVote(
            @Header("authorization") token: String,
            @Query("postId") postId: String,
            @Query("birthId") birthId: String
    ): Observable<VoteTotal>

    companion object {
        fun create(): CrostataApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.1.123:3000")
                    .build()

            return retrofit.create(CrostataApiService::class.java)
        }
    }
}