package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_profile_post.view.*
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel

class ProfilePostViewHolder(itemView: View, val profileViewModel: ProfileViewModel)
    : RecyclerView.ViewHolder(itemView) {

    val TAG: String = javaClass.simpleName
    private var listenerPost: ProfileViewModel = profileViewModel

    fun init(post: Post) {
        profileViewModel.loadProfileImage(itemView.profileImage, 128, true)

        Log.v(TAG, post.contentType + "ww")
        if (post.contentType == "IT") {
            itemView.imageView.visibility = View.VISIBLE
            profileViewModel.loadPostedImage(itemView.imageView, post, 1080)
            itemView.imageView.setOnClickListener { listenerPost.openFullPost(post) }
        } else {
            itemView.imageView.visibility = View.GONE
            itemView.imageView.requestLayout()
            clearView()
        }

        itemView.profileName.text = post.creatorName

        itemView.textPost.text = post.text
        itemView.textPost.setOnClickListener { listenerPost.openFullPost(post) }
    }

    private fun clearView() {
        listenerPost.clearPostedImageGlide(itemView.imageView)
    }
}