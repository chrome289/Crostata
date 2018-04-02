package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_patriot_chart.view.*
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel

class ProfileCommentViewHolder(itemView: View, profileViewModel: ProfileViewModel) : RecyclerView.ViewHolder(itemView) {

    fun init(comment:Comment){
        itemView.profileName.text = comment.name

    }
}