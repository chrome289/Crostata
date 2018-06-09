package xyz.siddharthseth.crostata.util.recyclerView

import android.content.res.ColorStateList
import android.widget.ImageView
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.LikeTotal
import xyz.siddharthseth.crostata.data.model.retrofit.Post

interface PostItemListener {
    fun clearPostedImageGlide(imageView: ImageView)
    fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView)
    fun loadProfileImage(post: Post, dimen: Int, imageView: ImageView)

    fun handleLike(index: Int)
    fun addLike(post: Post): Observable<LikeTotal>
    fun clearLike(post: Post): Observable<LikeTotal>

    val likeColorTint: ColorStateList
    val reportColorTint: ColorStateList
    val grey900: ColorStateList
    val grey500: ColorStateList

    fun openFullPost(index: Int)
    fun openProfile(index: Int)
}