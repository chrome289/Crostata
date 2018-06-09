package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.Comment

interface CommentItemListener {
    fun onReportButtonClick(index: Int): Observable<Boolean>
    fun onCommentButtonClick(comment: String): Observable<Boolean>

    fun openProfile(index: Int)

    fun clearPostedImageGlide(imageView: ImageView)
    fun loadProfileImage(comment: Comment, dimen: Int, imageView: ImageView)
}