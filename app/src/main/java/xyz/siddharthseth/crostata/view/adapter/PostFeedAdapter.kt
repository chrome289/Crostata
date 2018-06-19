package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.util.recyclerView.FooterListener
import xyz.siddharthseth.crostata.util.recyclerView.PostItemListener
import xyz.siddharthseth.crostata.view.adapter.viewholder.FooterViewHolder
import xyz.siddharthseth.crostata.view.adapter.viewholder.PostViewHolder
import java.util.*

/**
 *adapter for post feed
 */
class PostFeedAdapter(private val postItemListener: PostItemListener, private val footerListener: FooterListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemView) {
            PostViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.recyclerview_post_card, parent, false)
                    , postItemListener
            )
        } else {
            FooterViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.recyclerview_loading_footer, parent, false)
                    , footerListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < postList.size - 1)
            itemView
        else
            footerView
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            holder.init(postList[holder.adapterPosition])
            holder.loadImages(postList[holder.adapterPosition])
        } else if (holder is FooterViewHolder) {
            holder.init(isSecondLoaderVisible, isErrorVisible, isEndVisible)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is PostViewHolder) {
            holder.clearImages()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    var postList = ArrayList<Post>()
    var isSecondLoaderVisible = false
    var isErrorVisible = false
    var isEndVisible: Boolean = false

    //static stuff
    companion object {
        private val TAG: String = this::class.java.simpleName
        private const val itemView = 1
        private const val footerView = 2
    }
}