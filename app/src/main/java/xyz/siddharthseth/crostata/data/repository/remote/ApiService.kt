package xyz.siddharthseth.crostata.data.repository.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.siddharthseth.crostata.domain.model.remote.JsonResponse

interface ApiService {

    @GET("feed/{userId}/posts")
    suspend fun getProfileFeed(
        @Path("userId") userId: String,
        @Query("cursor") cursor: String?
    ): Response<JsonResponse>

    @GET("feed/{userId}")
    suspend fun getFeed(
        @Path("userId") userId: String,
        @Query("cursor") cursor: String?
    ): Response<JsonResponse>

    @GET("users/{username}?username=1")
    suspend fun getProfileByUsername(
        @Path("username") username: String,
    ): Response<JsonResponse>

    @GET("users/{userId}?username=0")
    suspend fun getProfile(
        @Path("userId") userId: String,
    ): Response<JsonResponse>

    @GET("posts/{postId}")
    suspend fun getPost(
        @Path("postId") postId: String,
        @Query("cursor") cursor: String?
    ): Response<JsonResponse>
}