package xyz.siddharthseth.crostata.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.domain.model.remote.Post
import java.lang.reflect.Type

class ReplyPagingSource(val apiManager: ApiManager, var postId: String) :
    PagingSource<String, Post>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        val after: String? = params.key
        val response = apiManager.getPost(postId, after)
        val pagedResponse = response.body()

        //
        val feedPostList: Type = Types.newParameterizedType(
            MutableList::class.java,
            Post::class.java
        )
        var adapter: JsonAdapter<List<Post>> =
            Moshi.Builder().build().adapter(feedPostList)
        adapter = adapter.lenient()
        val t: MutableList<Post> =
            adapter.fromJsonValue(pagedResponse?.data)?.toMutableList() ?: arrayListOf()
        //
        t.removeIf { it.replyTo == null }

        var nextPageNumber: String? = null
        if (pagedResponse?.links?.after != null) {
            nextPageNumber = pagedResponse.links.after
        }

        return LoadResult.Page(
            data = t,
            prevKey = null,
            nextKey = nextPageNumber
        )

    }

    override fun getRefreshKey(state: PagingState<String, Post>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}