package xyz.siddharthseth.crostata.domain.usecase.post

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.domain.model.remote.Post
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(var apiManager: ApiManager) {
    suspend fun execute(postId: String, cursor: String?): Post? {
        val response = apiManager.getPost(postId, cursor).body()
        var adapter: JsonAdapter<Post> = Moshi.Builder().build().adapter(Post::class.java)
        adapter = adapter.lenient()
        return adapter.fromJsonValue(response?.data)
    }
}