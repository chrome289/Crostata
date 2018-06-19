package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.Comment

/**
 * listener interface for comments
 */
interface CommentItemListener {
    /**
     * report the comment
     * @param index index of comment in list
     */
    fun onReportButtonClick(index: Int): Observable<Boolean>

    /**
     * post a comment
     * @param comment comment text, 1<=length<=10000
     */
    fun onCommentButtonClick(comment: String): Observable<Boolean>

    /**
     * open profile for commenter
     * @param index index of comment in list
     */
    fun openProfile(index: Int)

    /**
     * clear profile image
     * @param imageView view id of imageview
     */
    fun clearImageGlide(imageView: ImageView)

    /**
     * load profile image
     * @param comment comment object to fetch profileId
     * @param dimen intended resolution
     * @param imageView view id of imageview
     */
    fun loadProfileImage(comment: Comment, dimen: Int, imageView: ImageView)
}