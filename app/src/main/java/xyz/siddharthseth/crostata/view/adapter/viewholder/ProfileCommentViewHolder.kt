package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_profile_comment.view.*
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel

class ProfileCommentViewHolder(itemView: View, private val profileViewModel: ProfileViewModel)
    : RecyclerView.ViewHolder(itemView) {

    private var listenerPost: ProfileViewModel = profileViewModel
    val TAG: String = javaClass.simpleName

    fun init(comment: Comment) {
        itemView.comment.text = comment.text
        Log.v(TAG, "comments " + comment.post.creatorName)
        itemView.content_name.text = comment.post.creatorName

        profileViewModel.loadProfileImage(itemView.profileImage, true)

        if (comment.post.contentType == "IT") {
            itemView.content_image.visibility = View.VISIBLE
            profileViewModel.loadPostedImage(itemView.content_image, comment.post)
            clearView()
        } else {
            itemView.content_image.visibility = View.GONE
            itemView.content_image.requestLayout()
        }
    }

    private fun clearView() {
        listenerPost.clearPostedImageGlide(itemView.content_image)
    }
}