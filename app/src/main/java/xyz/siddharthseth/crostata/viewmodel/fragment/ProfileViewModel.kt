package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import java.util.*
import kotlin.collections.ArrayList

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val token = SharedPrefrencesService().getToken(application)
    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val TAG: String = javaClass.simpleName
    private val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
    private var size: Int = 5
    private var lastTimestamp: Float = Calendar.getInstance().timeInMillis / 1000.0f
    private var commentList = ArrayList<Comment>()
    private var postList = ArrayList<Post>()
    private var isInitialized = false

    fun getInfo(): Observable<Subject> {
        return contentRepository.getSubjectInfo(token, birthId)
                .subscribeOn(Schedulers.io())
    }

    fun getComments(): Observable<Comment> {
        if (isInitialized)
            return Observable.from(commentList)
        else
            return getNextComments()
    }


    private fun getNextComments(): Observable<Comment> {
        return contentRepository.getSubjectComments(token, birthId, size, lastTimestamp)
                .subscribeOn(Schedulers.io())
                .flatMap { return@flatMap Observable.from(it) }
    }


    fun getPosts(): Observable<Post> {
        if (isInitialized)
            return Observable.from(postList)
        else
            return getNextPosts()
    }

    private fun getNextPosts(): Observable<Post> {
        return contentRepository.getSubjectPosts(token, birthId, size, lastTimestamp)
                .subscribeOn(Schedulers.io())
                .flatMap { return@flatMap Observable.from(it) }
    }


    fun loadProfileImage(imageView: ImageView, dimen: Int, isCircle: Boolean) {
        val context: Context = getApplication()
        val quality = 70
        //TODO birthid hardcoded
        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/subject/profileImage?birthId=062912952&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        if (isCircle) {
            GlideApp.with(context)
                    .load(glideUrl)
                    .placeholder(R.drawable.home_feed_content_placeholder)
                    .centerInside()
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.IMMEDIATE)
                    .into(imageView)
        } else {
            GlideApp.with(context)
                    .load(glideUrl)
                    .placeholder(R.drawable.home_feed_content_placeholder)
                    .centerInside()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.IMMEDIATE)
                    .into(imageView)
        }
    }

    fun loadPostedImage(imageView: ImageView, post: Post, dimen: Int) {
        val context: Context = getApplication()

        val quality = 70
        val imageId = post.imageId

        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/content/postedImage?imageId=$imageId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        Log.v(TAG, "token-" + token)
        GlideApp.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.palmyra)
                .fitCenter()
                .override(1080)
                .into(imageView)
    }

    fun clearPostedImageGlide(contentImage: ImageView?) {
        val context: Context = getApplication()
        GlideApp.with(context).clear(contentImage as View)
    }
}