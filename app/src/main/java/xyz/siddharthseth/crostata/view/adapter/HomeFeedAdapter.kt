package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.util.recyclerView.listeners.PostItemListener
import xyz.siddharthseth.crostata.view.adapter.viewholder.PostViewHolder
import java.util.*

class HomeFeedAdapter(private val postItemListener: PostItemListener) : RecyclerView.Adapter<PostViewHolder>() {

    var postList = ArrayList<Post>()
    var TAG: String = javaClass.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        Log.v(TAG, "onCreateViewHolder")
        return PostViewHolder(
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.recyclerview_home_card, parent, false)
                , postItemListener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder")
        holder.init(postList[holder.adapterPosition])
        holder.loadImages(postList[holder.adapterPosition])
    }

    override fun onViewRecycled(holder: PostViewHolder) {
        super.onViewRecycled(holder)
        holder.clearImages()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}