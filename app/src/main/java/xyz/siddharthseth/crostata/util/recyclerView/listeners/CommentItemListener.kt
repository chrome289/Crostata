package xyz.siddharthseth.crostata.util.recyclerView.listeners

import android.widget.ImageView
import xyz.siddharthseth.crostata.data.model.Comment

interface CommentItemListener {
    fun onReportButtonClick(postId: String)

    fun openProfile(birthId: String)

    fun clearPostedImageGlide(imageView: ImageView)
    fun loadProfileImage(comment: Comment, dimen: Int, imageView: ImageView)
}