package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal

interface PostRecyclerViewListener {
    fun onCommentButtonClick(postId: String)
    fun onReportButtonClick(postId: String)

    fun clearPostedImageGlide(imageView: ImageView)
    fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView)
    fun loadProfileImage(creatorId: String, dimen: Int, isCircle: Boolean, imageView: ImageView)

    fun onClearVote(postId: String): Observable<VoteTotal>
    fun onVoteButtonClick(postId: String, value: Int): Observable<VoteTotal>

    val voteColorTint: Int
    val reportColorTint: Int
    val greyUnselected: Int

    fun openFullPost(post: Post)
    fun openProfile(birthId: String)
}