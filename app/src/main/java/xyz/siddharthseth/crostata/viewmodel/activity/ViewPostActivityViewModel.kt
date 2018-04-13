package xyz.siddharthseth.crostata.viewmodel.activity

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
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.util.recyclerView.CommentRecyclerViewListener
import java.util.*

class ViewPostActivityViewModel(application: Application) : AndroidViewModel(application), CommentRecyclerViewListener {
    var post: Post = Post()
    private var commentList = ArrayList<Comment>()
    private val sharedPreferencesService = SharedPrefrencesService()
    private var token: String = sharedPreferencesService.getToken(getApplication())
    val TAG: String = javaClass.simpleName
    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private var noOfComments: Int = 10
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var isInitialized = false

    fun clearPostedImageGlide(imageView: ImageView) {
        val context: Context = getApplication()
        GlideApp.with(context).clear(imageView as View)
    }

    fun loadPostedImage(imageView: ImageView) {
        val context: Context = getApplication()

        Log.v(TAG, "imageId-" + post.imageId)
        val dimen = 1080
        val quality = 70
        val imageId = post.imageId

        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/content/postedImage?imageId=$imageId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        Log.v(TAG, "width like ---" + imageView.width)
        GlideApp.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .downsample(DownsampleStrategy.FIT_CENTER)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.LOW)
                .fitCenter()
                .override(1080)
                .into(imageView)
    }

    override fun loadProfileImage(birthId: String, profileImage: ImageView) {
        val context: Context = getApplication()
        val dimen = 128
        val quality = 70
        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/subject/profileImage?birthId=$birthId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        GlideApp.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .centerInside()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.IMMEDIATE)
                .into(profileImage)
    }

    fun getComments(): Observable<Comment> {
        return contentRepository.getComments(token, post._id, noOfComments, lastTimestamp)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    if (!isInitialized)
                        isInitialized = true
                    Log.v(TAG, "commentList " + commentList.size + " it.commments " + it.size)
                    commentList.addAll(it)
                    commentList.sort()
                    Log.v(TAG, "commentList " + commentList.size + " it.commments " + it.size)
                    lastTimestamp = if (commentList.size > 0) commentList[commentList.size - 1].getTimestamp()
                    else Calendar.getInstance().timeInMillis
                    return@flatMap Observable.from(it)
                }
    }
}