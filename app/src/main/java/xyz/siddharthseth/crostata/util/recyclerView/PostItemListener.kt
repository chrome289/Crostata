package xyz.siddharthseth.crostata.util.recyclerView

import android.content.res.ColorStateList
import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal

interface PostItemListener {
    fun onCommentButtonClick(comment: String): Observable<Boolean>
    fun onReportButtonClick(post: Post)

    fun clearPostedImageGlide(imageView: ImageView)
    fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView)
    fun loadProfileImage(post: Post, dimen: Int, imageView: ImageView)

    fun onClearVote(postId: String): Observable<VoteTotal>

    val upVoteColorTint: ColorStateList
    val downVoteColorTint: ColorStateList
    val reportColorTint: ColorStateList
    val extraDarkGrey: ColorStateList
    val unSelectedGrey: ColorStateList

    fun openFullPost(index: Int)
    fun handleVote(post: Post, value: Int)
    fun openProfile(index: Int)
}