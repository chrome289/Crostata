package xyz.siddharthseth.crostata.data.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.ChartEntry
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
    fun getPosts(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String,
            @Query("noOfPosts") noOfPosts: Int,
            @Query("lastTimestamp") lastTimestamp: Long
    ): Observable<List<Post>>

    @POST("/api/opinion/vote")
    @FormUrlEncoded
    fun addVote(
            @Header("authorization") token: String,
            @Field("postId") postId: String,
            @Field("birthId") birthId: String,
            @Field("value") value: Int
    ): Observable<VoteTotal>

    @DELETE("/api/opinion/vote")
    fun deleteVote(
            @Header("authorization") token: String,
            @Query("postId") postId: String,
            @Query("birthId") birthId: String
    ): Observable<VoteTotal>

    /*@GET("/api/content/imageMetadata")
    fun imageMetadata(
            @Header("authorization") token: String,
            @Query("postId") postId: String
    ): Observable<ImageMetadata>*/

    @GET("/api/opinion/comments")
    fun getComments(
            @Header("authorization") token: String,
            @Query("postId") postId: String,
            @Query("noOfComments") noOfComments: Int,
            @Query("lastTimestamp") lastTimestamp: Long
    ): Observable<List<Comment>>

    @GET("/api/subject/charts")
    fun getPatriotChart(
            @Header("authorization") token: String
    ): Observable<List<ChartEntry>>

    @GET("/api/subject/info")
    fun getSubjectInfo(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String
    ): Observable<xyz.siddharthseth.crostata.data.model.retrofit.Subject>

    @GET("/api/subject/comments")
    fun getProfileComments(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String,
            @Query("noOfComments") noOfComments: Int,
            @Query("lastTimestamp") lastTimestamp: Float
    ): Observable<List<Comment>>

    @GET("/api/subject/posts")
    fun getProfilePosts(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String,
            @Query("size") noOfPosts: Int,
            @Query("lastTimestamp") lastTimestamp: Float
    ): Observable<List<Post>>

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