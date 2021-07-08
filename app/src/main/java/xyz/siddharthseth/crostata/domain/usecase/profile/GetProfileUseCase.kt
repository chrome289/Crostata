package xyz.siddharthseth.crostata.domain.usecase.profile

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.domain.model.User
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(var apiManager: ApiManager) {
    suspend fun execute(userId: String): User {
        val response = apiManager.getProfile(userId).body()
        var adapter: JsonAdapter<User> = Moshi.Builder().build().adapter(User::class.java)
        adapter = adapter.lenient()
        return adapter.fromJsonValue(response?.data)!!
    }
}