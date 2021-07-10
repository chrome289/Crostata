package xyz.siddharthseth.crostata.ui.viewmodel.activity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.domain.model.remote.FeedPost
import xyz.siddharthseth.crostata.domain.usecase.profile.GetProfileUseCase
import xyz.siddharthseth.crostata.ui.adapter.FeedRecyclerViewAdapter
import xyz.siddharthseth.crostata.ui.paging.ProfileFeedPagingSource
import xyz.siddharthseth.crostata.util.TimeConverter
import javax.inject.Inject

class ProfileActivityViewModel
@Inject constructor(
    app: Application,
    private val apiManager: ApiManager,
    timeConverter: TimeConverter,
    private val getProfileUseCase: GetProfileUseCase,
    sharedPreferencesDao: SharedPreferencesDao
) : ViewModel() {

    fun getProfile(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getProfileUseCase.execute(userId, isUsername = !(userId.contains("-")))
            if (user != null) {
                getPosts(user.userId)
                    .collect {
                        postRecyclerViewAdapter.submitData(it)
                    }
            }
        }
    }

    private fun getPosts(userId: String): Flow<PagingData<FeedPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                ProfileFeedPagingSource(apiManager = apiManager, userId)
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
        val TAG: String = ProfileActivityViewModel::class.java.simpleName
    }

}