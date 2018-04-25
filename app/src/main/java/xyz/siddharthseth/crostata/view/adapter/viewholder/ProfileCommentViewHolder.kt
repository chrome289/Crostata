package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_profile_comment.view.*
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel

class ProfileCommentViewHolder(itemView: View, private val profileViewModel: ProfileViewModel)
    : RecyclerView.ViewHolder(itemView) {

    private var listenerComment: ProfileViewModel = profileViewModel
    val TAG: String = javaClass.simpleName

    fun init(comment: Comment) {
        itemView.comment.text = comment.text
        Log.v(TAG, "comments " + comment.post.creatorName)
        itemView.content_name.text = comment.post.creatorName

        /*profileViewModel.loadProfileImage(comment.birthId, 128, true, itemView.profileImage)

        if (comment.post.contentType == "IT") {
            itemView.contentImage.visibility = View.VISIBLE
            profileViewModel.loadPostedImage(comment.post, 256, itemView.contentImage)
        } else {
            itemView.contentImage.visibility = View.GONE
            itemView.contentImage.requestLayout()
            clearView()
        }*/
    }

    private fun clearView() {
        listenerComment.clearPostedImageGlide(itemView.contentImage)
    }
}