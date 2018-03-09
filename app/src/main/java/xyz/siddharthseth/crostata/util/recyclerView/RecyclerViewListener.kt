package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal

interface RecyclerViewListener {
    fun onVoteButtonClick(postId: String, value: Int): Observable<VoteTotal>
    fun onCommentButtonClick(postId: String)
    fun onReportButtonClick(postId: String)
    fun loadPostedImage(postId: String, imageView: ImageView)
    fun loadProfileImage(creatorId: String, imageView: ImageView)
    fun onClearVote(postId: String): Observable<VoteTotal>
}