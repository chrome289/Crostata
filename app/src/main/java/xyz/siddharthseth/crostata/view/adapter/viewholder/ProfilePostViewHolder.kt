package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_profile_post.view.*
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel

class ProfilePostViewHolder(itemView: View, val profileViewModel: ProfileViewModel)
    : RecyclerView.ViewHolder(itemView) {

    private var listenerPost: ProfileViewModel = profileViewModel

    fun init(post: Post) {
        profileViewModel.loadProfileImage(itemView.profileImage, true)

        if (post.contentType == "IT") {
            itemView.imageView.visibility = View.VISIBLE
            profileViewModel.loadPostedImage(itemView.imageView, post)
            clearView()
        } else {
            itemView.imageView.visibility = View.GONE
            itemView.imageView.requestLayout()
        }
    }

    private fun clearView() {
        listenerPost.clearPostedImageGlide(itemView.imageView)
    }
}