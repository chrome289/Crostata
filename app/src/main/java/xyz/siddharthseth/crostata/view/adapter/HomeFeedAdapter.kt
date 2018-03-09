package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.modelView.HomeFeedViewModel
import xyz.siddharthseth.crostata.view.adapter.viewholder.AdapterPost
import java.util.*

class HomeFeedAdapter(private val homeFeedViewModel: HomeFeedViewModel) : RecyclerView.Adapter<AdapterPost>() {

    var postList = ArrayList<Post>()
    var _tag = "HomeFeedAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPost {
        return AdapterPost(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_home_card, parent, false)
                , homeFeedViewModel)
    }

    override fun onBindViewHolder(holder: AdapterPost, position: Int) {
        holder.init(postList[position])
    }


    override fun getItemCount(): Int {
        return postList.size
    }

    fun getItem(position: Int): Post {
        return postList[position]
    }

    fun sortList() {
        postList.sort()
    }
}