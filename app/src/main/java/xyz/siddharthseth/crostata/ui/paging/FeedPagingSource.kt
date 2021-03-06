package xyz.siddharthseth.crostata.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.domain.model.remote.FeedPost
import java.lang.reflect.Type

class FeedPagingSource(val apiManager: ApiManager) : PagingSource<String, FeedPost>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, FeedPost> {
        val after: String? = params.key
        val response = apiManager.getFeed("0f6818e9-ce1a-437a-9a59-8f4536b2d79d", after)
        val pagedResponse = response.body()

        //
        val feedPostList: Type = Types.newParameterizedType(
            MutableList::class.java,
            FeedPost::class.java
        )
        var adapter: JsonAdapter<List<FeedPost>> =
            Moshi.Builder().build().adapter(feedPostList)
        adapter = adapter.lenient()
        val t: List<FeedPost> = adapter.fromJsonValue(pagedResponse?.data).orEmpty()
        //

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

    override fun getRefreshKey(state: PagingState<String, FeedPost>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}