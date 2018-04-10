package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.SingleLivePost
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.ImageMetadata
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.util.recyclerView.PostRecyclerViewListener
import java.util.*
import kotlin.collections.ArrayList


class HomeFeedViewModel(application: Application) : AndroidViewModel(application)
        , PostRecyclerViewListener {

    private val TAG = javaClass.simpleName
    private val sharedPreferencesService = SharedPrefrencesService()
    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private var token: String = sharedPreferencesService.getToken(getApplication())
    private var noOfPosts: Int = 10
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var isInitialized = false
    var mutablePost: SingleLivePost = SingleLivePost()
    private var postList: ArrayList<Post> = ArrayList()

    override fun openFullPost(post: Post) {
        mutablePost.value = post
    }

    override val greyUnselected: Int
        get() = ContextCompat.getColor(getApplication(), R.color.greyUnselected)
    override val voteColorTint: Int
        get() = ContextCompat.getColor(getApplication(), R.color.voteSelected)
    override val commentColorTint: Int
        get() = ContextCompat.getColor(getApplication(), R.color.commentSelected)
    override val reportColorTint: Int
        get() = ContextCompat.getColor(getApplication(), R.color.reportSelected)
    var isLoading: Boolean = false


    override fun clearPostedImageGlide(imageView: ImageView) {
        val context: Context = getApplication()
        GlideApp.with(context).clear(imageView as View)
    }

    override fun loadPostedImage(post: Post, imageView: ImageView) {
        val context: Context = getApplication()

        val dimen = 1080
        val quality = 70
        val postId = post.postId

        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/content/postedImage?postId=$postId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        // Log.v(TAG, "width like ---" + imageView.width)
        GlideApp.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .priority(Priority.LOW)
                .fitCenter()
                .override(1080)
                .into(imageView)
    }

    override fun loadProfileImage(creatorId: String, imageView: ImageView) {
        val context: Context = getApplication()
        val dimen = 128
        val quality = 70
        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/subject/profileImage?birthId=$creatorId&dimen=$dimen&quality=$quality"
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
                .into(imageView)
    }

    override fun onVoteButtonClick(postId: String, value: Int): Observable<VoteTotal> {
        Log.v(TAG, "upVoteButtonClick")
        val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
        return contentRepository.submitVote(token, postId, birthId, value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())//.map({ it -> return@map it })
    }

    override fun onClearVote(postId: String): Observable<VoteTotal> {
        Log.v(TAG, "upVoteButtonClick")
        val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
        return contentRepository.clearVote(token, postId, birthId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())//.map({ it -> return@map it })
    }

    override fun onCommentButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReportButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getPosts(): Observable<Post> {
        if (isInitialized)
            return Observable.from(postList)
        else
            return fetchPosts()
    }

    private fun fetchPosts(): Observable<Post> {
        val birthId = sharedPreferencesService.getUserDetails(getApplication())
        isLoading = true
        Log.v(TAG, "making a request for posts")

        return contentRepository.getNextPosts(token, noOfPosts, lastTimestamp, birthId.birthId)
                .subscribeOn(Schedulers.io())
                .flatMap({ nextPosts ->
                    //Thread.sleep(5000)
                    if (!isInitialized)
                        isInitialized = true
                    postList.addAll(nextPosts)
                    postList.sort()
                    lastTimestamp = postList[postList.size - 1].getTimestamp()
                    isLoading = false
                    return@flatMap Observable.from(nextPosts)
                })
                .flatMap(
                        { post: Post ->
                            if (post.contentType == "TO")
                                return@flatMap Observable.just(ImageMetadata())
                            else
                                return@flatMap contentRepository.getImageMetadata(token, post.postId)
                        }
                        , { post: Post, imageMetadata: ImageMetadata -> post.metadata = imageMetadata;return@flatMap post }
                )
    }

    fun getNextPosts(): Observable<Post> {
        return fetchPosts()
    }
}