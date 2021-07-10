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
import xyz.siddharthseth.crostata.domain.model.remote.Post
import xyz.siddharthseth.crostata.domain.usecase.post.GetPostsUseCase
import xyz.siddharthseth.crostata.ui.adapter.CommentRecyclerViewAdapter
import xyz.siddharthseth.crostata.ui.paging.ReplyPagingSource
import xyz.siddharthseth.crostata.util.TimeConverter
import javax.inject.Inject

class PostActivityViewModel
@Inject constructor(
    app: Application,
    private val apiManager: ApiManager,
    timeConverter: TimeConverter,
    private val getPostsUseCase: GetPostsUseCase,
    sharedPreferencesDao: SharedPreferencesDao
) : ViewModel() {

    fun getPost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getComments(postId)
                .collect {
                    commentRecyclerViewAdapter.submitData(it)
                }
        }
    }

    private fun getComments(postId: String): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                ReplyPagingSource(apiManager = apiManager, postId)
            }
        ).flow.cachedIn(viewModelScope)
    }

    var commentRecyclerViewAdapter =
        CommentRecyclerViewAdapter(
            timeConverter,
            sharedPreferencesDao,
            object : DiffUtil.ItemCallback<Post>() {
                override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                    return oldItem == newItem
                }

                override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                    return oldItem.postId == newItem.postId
                }
            })

    companion object {
        val TAG: String = PostActivityViewModel::class.java.simpleName
    }

}