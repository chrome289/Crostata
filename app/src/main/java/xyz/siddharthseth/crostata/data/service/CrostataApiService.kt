package xyz.siddharthseth.crostata.data.service

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.CheckToken
import xyz.siddharthseth.crostata.data.model.retrofit.NextPosts
import xyz.siddharthseth.crostata.data.model.retrofit.Token

interface CrostataApiService {

    @POST("/api/auth/login")
    @FormUrlEncoded
    fun signIn(
            @Field("birth_id") birthId: String,
            @Field("password") password: String
    ): Observable<Response<Token>>

    @POST("/api/auth/loginToken")
    fun signInSilently(
            @Header("authorization") token: String
    ): Observable<CheckToken>

    @GET("/api/content/nextPostsList")
    fun nextPostsList(
            @Header("authorization") token: String,
            @Query("noOfPosts") noOfPosts: Int,
            @Query("lastTimestamp") lastTimestamp: Float
    ): Observable<NextPosts>

    @GET("/api/content/textPost")
    fun textPost(
            @Header("authorization") token: String,
            @Query("post_id") postId: String
    ): Observable<Post>

    @POST("/api/content/textPost")
    @FormUrlEncoded
    fun submitVote(
            @Header("authorization") token: String,
            @Field("post_id") postId: String,
            @Field("birth_id") birthId: String,
            @Field("value") value: Int): Observable<Boolean>

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