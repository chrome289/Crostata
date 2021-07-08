package xyz.siddharthseth.crostata.domain.usecase.feed

import retrofit2.Response
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.domain.model.remote.JsonResponse
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(var apiManager: ApiManager) {
    suspend fun execute(userId: String): Response<JsonResponse> {
        return apiManager.getFeed(userId, "")
    }
}