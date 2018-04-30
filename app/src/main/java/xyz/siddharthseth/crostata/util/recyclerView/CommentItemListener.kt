package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post

interface CommentItemListener {
    fun onReportButtonClick(post: Post)

    fun openProfile(birthId: String)

    fun clearPostedImageGlide(imageView: ImageView)
    fun loadProfileImage(comment: Comment, dimen: Int, imageView: ImageView)
}