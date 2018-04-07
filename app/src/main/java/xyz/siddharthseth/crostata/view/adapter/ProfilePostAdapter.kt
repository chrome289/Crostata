package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.view.adapter.viewholder.ProfilePostViewHolder
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel

class ProfilePostAdapter(private val profileViewModel: ProfileViewModel)
    : RecyclerView.Adapter<ProfilePostViewHolder>() {

    var postList = ArrayList<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_profile_post, parent, false)
        return ProfilePostViewHolder(view, profileViewModel)
    }


    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ProfilePostViewHolder, position: Int) {
        holder.init(postList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}