package xyz.siddharthseth.crostata.util.recyclerView

import android.content.res.ColorStateList
import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.LikeTotal
import xyz.siddharthseth.crostata.data.model.retrofit.Post

/**
 * listener interface for posts
 */
interface PostItemListener {

    /**
     * handle like action
     * @param index index for post whose like is to handled
     */
    fun handleLike(index: Int)

    /**
     * add like to post
     * @param post Post for whom like is to added
     */
    fun addLike(post: Post): Observable<LikeTotal>

    /**
     * clear like from post
     * @param post Post whose like is to be removed
     */
    fun clearLike(post: Post): Observable<LikeTotal>

    /**
     * like color
     */
    val likeColorTint: ColorStateList

    /**
     * report color
     */
    val reportColorTint: ColorStateList

    /**
     * darkest grey
     */
    val grey900: ColorStateList

    /**
     * medium grey
     */
    val grey500: ColorStateList

    /**
     * open full post
     * @param index index for post in list
     */
    fun openFullPost(index: Int)

    /**
     * open a profile
     * @param index index for post whose profile link was clicked
     */
    fun openProfile(index: Int)

    /**
     * clear profile image
     * @param imageView view id of imageview
     */
    fun clearImageGlide(imageView: ImageView)

    /**
     * load posted image
     * @param post post object to fetch profileId
     * @param dimen intended resolution
     * @param imageView view id of imageview
     */
    fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView)

    /**
     * load profile image
     * @param post post object to fetch profileId
     * @param dimen intended resolution
     * @param imageView view id of imageview
     */
    fun loadProfileImage(post: Post, dimen: Int, imageView: ImageView)
}