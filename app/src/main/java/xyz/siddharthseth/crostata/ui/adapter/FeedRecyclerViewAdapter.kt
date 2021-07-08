package xyz.siddharthseth.crostata.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.databinding.ViewCardFeedPostBinding
import xyz.siddharthseth.crostata.domain.model.remote.FeedPost
import xyz.siddharthseth.crostata.ui.adapter.viewholder.PostViewHolder
import xyz.siddharthseth.crostata.util.TimeConverter

class FeedRecyclerViewAdapter(
    private val timeConverter: TimeConverter,
    private val sharedPreferencesDao: SharedPreferencesDao,
    diffCallback: DiffUtil.ItemCallback<FeedPost>
) : PagingDataAdapter<FeedPost, PostViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            binding = ViewCardFeedPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), timeConverter, sharedPreferencesDao
        )
    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let { holder.execute(it) }
    }

}