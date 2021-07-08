package xyz.siddharthseth.crostata.ui.viewmodel.fragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.flow.Flow
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.domain.model.remote.FeedPost
import xyz.siddharthseth.crostata.domain.usecase.feed.GetFeedUseCase
import xyz.siddharthseth.crostata.ui.adapter.FeedRecyclerViewAdapter
import xyz.siddharthseth.crostata.ui.paging.PostPagingSource
import xyz.siddharthseth.crostata.util.TimeConverter
import javax.inject.Inject

class HomeFragmentViewModel
@Inject constructor(
    app: Application,
    private val apiManager: ApiManager,
    timeConverter: TimeConverter,
    private val getFeedUseCase: GetFeedUseCase,
    sharedPreferencesDao: SharedPreferencesDao
) : ViewModel() {
    fun syncPosts(): Flow<PagingData<FeedPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                PostPagingSource(apiManager = apiManager)
            }
        ).flow.cachedIn(viewModelScope)
    }

    var postRecyclerViewAdapter =
        FeedRecyclerViewAdapter(timeConverter, sharedPreferencesDao, object :
            DiffUtil.ItemCallback<FeedPost>() {
            override fun areContentsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
                return oldItem.postId == newItem.postId
            }
        })

    companion object {
        val TAG: String = HomeFragmentViewModel::class.java.simpleName
    }

}