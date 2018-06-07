package xyz.siddharthseth.crostata.util.recyclerView

import android.content.res.ColorStateList
import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.LikeTotal
import xyz.siddharthseth.crostata.data.model.retrofit.Post

interface PostItemListener {
    fun onCommentButtonClick(comment: String): Observable<Boolean>
    fun onReportButtonClick(post: Post)

    fun clearPostedImageGlide(imageView: ImageView)
    fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView)
    fun loadProfileImage(post: Post, dimen: Int, imageView: ImageView)

    fun onClearLike(postId: String): Observable<LikeTotal>

    val likeColorTint: ColorStateList
    val reportColorTint: ColorStateList
    val grey900: ColorStateList
    val grey500: ColorStateList

    fun openFullPost(index: Int)
    fun handleLike(post: Post, value: Int)
    fun openProfile(index: Int)
}