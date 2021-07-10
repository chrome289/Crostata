package xyz.siddharthseth.crostata.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.databinding.ViewCardPostCommentBinding
import xyz.siddharthseth.crostata.domain.model.remote.Post
import xyz.siddharthseth.crostata.ui.adapter.viewholder.CommentViewHolder
import xyz.siddharthseth.crostata.util.TimeConverter

class CommentRecyclerViewAdapter(
    private val timeConverter: TimeConverter,
    private val sharedPreferencesDao: SharedPreferencesDao,
    diffCallback: DiffUtil.ItemCallback<Post>
) : PagingDataAdapter<Post, CommentViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            binding = ViewCardPostCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), timeConverter, sharedPreferencesDao
        )
    }


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        getItem(position)?.let { holder.execute(it) }
    }

}
