package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.view.adapter.viewholder.ProfileCommentViewHolder
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel

class ProfileCommentAdapter(private val profileViewModel: ProfileViewModel)
    : RecyclerView.Adapter<ProfileCommentViewHolder>() {

    var commentList = ArrayList<Comment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileCommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_profile_comment, parent, false)
        return ProfileCommentViewHolder(view, profileViewModel)
    }


    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: ProfileCommentViewHolder, position: Int) {
        holder.init(commentList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}