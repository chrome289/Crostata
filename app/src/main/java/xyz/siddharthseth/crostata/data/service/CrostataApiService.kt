package xyz.siddharthseth.crostata.data.service

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.*

interface CrostataApiService {

    //auth endpoints
    @POST("auth/login")
    @FormUrlEncoded
    fun signIn(
            @Field("birthId") birthId: String,
            @Field("password") password: String
    ): Observable<Response<Token>>

    @POST("auth/loginToken")
    fun signInSilently(
            @Header("authorization") token: String
    ): Observable<Response<ResponseBody>>


    //content endpoints
    @GET("content/nextPosts")
    fun getPosts(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String,
            @Query("lastTimestamp") lastTimestamp: Long
    ): Observable<Post.PostGlove>

    @GET("content/nextPosts")
    fun getPosts(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String,
            @Query("lastTimestamp") lastTimestamp: Long,
            @Query("requestId") requestId: String,
            @Query("after") after: Int
    ): Observable<Post.PostGlove>

    @POST("content/textPost")
    @FormUrlEncoded
    fun postTextPost(
            @Header("authorization") token: String,
            @Field("birthId") birthId: String,
            @Field("postContent") postContent: String,
            @Field("generate") generated: Boolean
    ): Observable<Response<ResponseBody>>

    @POST("content/comboPost")
    @Multipart
    fun postImagePost(
            @Header("authorization") token: String,
            @Part("birthId") birthId: RequestBody,
            @Part("postContent") postContent: RequestBody,
            @Part("file\"; fileName=\"myFile.png\" ") image: RequestBody,
            @Part("generate") generated: RequestBody
    ): Observable<Response<ResponseBody>>


    //opinion endpoints
    @POST("opinion/like")
    @FormUrlEncoded
    fun addLike(
            @Header("authorization") token: String,
            @Field("postId") postId: String,
            @Field("birthId") birthId: String,
            @Field("value") value: Int
    ): Observable<LikeTotal>

    @DELETE("opinion/like")
    fun deleteLike(
            @Header("authorization") token: String,
            @Query("postId") postId: String,
            @Query("birthId") birthId: String
    ): Observable<LikeTotal>

    @GET("opinion/comments")
    fun getComments(
            @Header("authorization") token: String,
            @Query("postId") postId: String,
            @Query("lastTimestamp") lastTimestamp: Long
    ): Observable<Comment.CommentGlove>

    @GET("opinion/comments")
    fun getComments(
            @Header("authorization") token: String,
            @Query("postId") postId: String,
            @Query("lastTimestamp") lastTimestamp: Long,
            @Query("requestId") requestId: String,
            @Query("after") after: Int
    ): Observable<Comment.CommentGlove>

    @POST("opinion/comment")
    @FormUrlEncoded
    fun addComment(
            @Header("authorization") token: String,
            @Field("postId") postId: String,
            @Field("birthId") birthId: String,
            @Field("text") comment: String,
            @Field("generate") generate: Boolean
    ): Observable<Comment>


    //subject endpoints
    @GET("subject/info")
    fun getSubjectInfo(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String
    ): Observable<Subject>

    @GET("subject/posts")
    fun getProfilePosts(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String,
            @Query("creatorId") creatorId: String,
            @Query("lastTimestamp") lastTimestamp: Long
    ): Observable<Post.PostGlove>

    @GET("subject/posts")
    fun getProfilePosts(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String,
            @Query("creatorId") creatorId: String,
            @Query("lastTimestamp") lastTimestamp: Long,
            @Query("requestId") requestId: String,
            @Query("after") after: Int
    ): Observable<Post.PostGlove>

    @GET("subject/posts")
    fun getServerStatus(
            @Header("authorization") token: String
    ): Observable<Response<ResponseBody>>


    //report endpoints
    @GET("report/reportMade")
    fun getReports(
            @Header("authorization") token: String,
            @Query("birthId") birthId: String
    ): Observable<List<Report>>


    //search endpoints
    @GET("search")
    fun getSearchResult(
            @Header("authorization") token: String,
            @Query("searchText") searchText: String
    ): Observable<SearchResult.SearchResultGlove>

    @GET("search")
    fun getSearchResult(
            @Header("authorization") token: String,
            @Query("searchText") searchText: String,
            @Query("requestId") requestId: String,
            @Query("after") after: Int
    ): Observable<SearchResult.SearchResultGlove>

    companion object {
        fun create(): CrostataApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.1.123:3000/api/v1/")
                    .build()

            return retrofit.create(CrostataApiService::class.java)
        }
    }
}