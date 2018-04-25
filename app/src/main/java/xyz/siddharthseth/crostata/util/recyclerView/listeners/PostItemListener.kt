package xyz.siddharthseth.crostata.util.recyclerView.listeners

import android.content.res.ColorStateList
import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal

interface PostItemListener {
    fun onCommentButtonClick(postId: String)
    fun onReportButtonClick(postId: String)

    fun clearPostedImageGlide(imageView: ImageView)
    fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView)
    fun loadProfileImage(post: Post, dimen: Int, imageView: ImageView)

    fun onClearVote(postId: String): Observable<VoteTotal>

    val upVoteColorTint: ColorStateList
    val downVoteColorTint: ColorStateList
    val reportColorTint: ColorStateList
    val extraDarkGrey: ColorStateList

    fun openFullPost(index: Int)
    fun handleVote(index: Int, value: Int)
    fun openProfile(birthId: String)
}