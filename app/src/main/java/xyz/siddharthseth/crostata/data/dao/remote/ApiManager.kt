package xyz.siddharthseth.crostata.data.dao.remote

import retrofit2.Response
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.data.repository.remote.ApiService
import xyz.siddharthseth.crostata.domain.model.remote.JsonResponse


class ApiManager(
    private val apiService: ApiService,
    var sharedPreferencesDao: SharedPreferencesDao
) {

    suspend fun getFeed(userId: String, cursor: String?): Response<JsonResponse> {
        return apiService.getFeed(userId, cursor)
    }

    suspend fun getProfileFeed(userId: String, cursor: String?): Response<JsonResponse> {
        return apiService.getProfileFeed(userId, cursor)
    }

    suspend fun getPost(postId: String, cursor: String?): Response<JsonResponse> {
        return apiService.getPost(postId, cursor)
    }

    suspend fun getProfileByUsername(username: String): Response<JsonResponse> {
        return apiService.getProfileByUsername(username)
    }

    suspend fun getProfile(userId: String): Response<JsonResponse> {
        return apiService.getProfile(userId)
    }


    companion object {
        const val RESULT_OK = 0
        const val RESULT_INCORRECT_PARAMS = 1
        const val RESULT_SERVER_ERROR = 2
        const val RESULT_NETWORK_ERROR = 3
    }
}