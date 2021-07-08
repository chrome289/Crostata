package xyz.siddharthseth.crostata.data.repository.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.siddharthseth.crostata.domain.model.remote.JsonResponse

interface ApiService {

    @GET("users/{userId}/feed")
    suspend fun getFeed(
        @Path("userId") userId: String,
        @Query("cursor") cursor: String?
    ): Response<JsonResponse>

    @GET("users/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: String,
    ): Response<JsonResponse>

}