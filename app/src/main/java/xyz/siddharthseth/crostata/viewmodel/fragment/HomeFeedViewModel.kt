package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideRequests
import xyz.siddharthseth.crostata.data.model.livedata.SingleBirthId
import xyz.siddharthseth.crostata.data.model.livedata.SingleLivePost
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.util.recyclerView.DiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.listeners.PostItemListener
import xyz.siddharthseth.crostata.view.adapter.HomeFeedAdapter
import java.util.*
import kotlin.collections.ArrayList


class HomeFeedViewModel(application: Application) : AndroidViewModel(application)
        , PostItemListener {

    override fun handleVote(index: Int, value: Int) {
        val post = postList[index]
        if (post.opinion == value) {
            onClearVote(post._id)
                    .subscribe(
                            { voteTotal: VoteTotal ->
                                Log.v(TAG, "success :" + voteTotal.success)
                                if (voteTotal.success) {
                                    post.opinion = 0
                                    post.votes = voteTotal.total
                                    updatePostItem(post, index)
                                }
                            }
                            , { error -> error.printStackTrace() })
        } else {
            onVoteButtonClick(post._id, value)
                    .subscribe(
                            { voteTotal: VoteTotal ->
                                Log.v(TAG, "success :" + voteTotal.success)
                                if (voteTotal.success) {
                                    post.votes = voteTotal.total
                                    post.opinion = value
                                    updatePostItem(post, index)
                                }
                            }
                            , { error -> error.printStackTrace() })
        }
    }

    private fun updatePostItem(post: Post, index: Int) {
        postList[index] = post
        val diffUtil = DiffUtil.calculateDiff(DiffUtilCallback(homeFeedAdapter.postList, postList))
        diffUtil.dispatchUpdatesTo(homeFeedAdapter)
    }

    override fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView) {
        val context: Context = getApplication()
        val imageId = post.imageId

        val quality = 80
        Log.v(TAG, "imageID-" + post.imageId + " type-" + post.contentType)
        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/content/postedImage?imageId=$imageId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        val dimenThumb = 48
        val qualityThumb = 50
        val glideUrlThumb = GlideUrl(context.getString(R.string.server_url) +
                "/api/content/postedImage?imageId=$imageId&dimen=$dimenThumb&quality=$qualityThumb"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        glide.load(glideUrl)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .thumbnail(glide.load(glideUrlThumb).priority(Priority.IMMEDIATE).fitCenter())
                .override(dimen, dimen / 2)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(imageView)
    }

    override fun loadProfileImage(creatorId: String, dimen: Int, isCircle: Boolean, imageView: ImageView) {
        val context: Context = getApplication()
        val quality = 60

        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/subject/profileImage?birthId=$creatorId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        glide.load(glideUrl)
                .priority(Priority.LOW)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(imageView)
    }

    override fun openFullPost(index: Int) {
        Log.v(TAG, "viewmodel click listener")
        mutablePost.value = postList[index]
    }

    override fun openProfile(birthId: String) {
        Log.v(TAG, "setting birthid")
        mutableBirthId.value = birthId
    }

    override val extraDarkGrey: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.extraDarkGrey))
    override val voteColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.voteSelected))
    override val reportColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.reportSelected))

    override fun clearPostedImageGlide(imageView: ImageView) {
        glide.clear(imageView as View)
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

    private val TAG = javaClass.simpleName
    private val sharedPreferencesService = SharedPrefrencesService()
    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private var token: String = sharedPreferencesService.getToken(getApplication())
    private var noOfPosts: Int = 10
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var isInitialized = false
    internal var isLoading: Boolean = false
    lateinit var glide: GlideRequests
    private var postList: ArrayList<Post> = ArrayList()
    var homeFeedAdapter: HomeFeedAdapter = HomeFeedAdapter(this)

    var mutablePost: SingleLivePost = SingleLivePost()
    var mutableBirthId: SingleBirthId = SingleBirthId()
    var width: Int = 1080

    init {
        homeFeedAdapter.setHasStableIds(true)
    }

    fun getPosts() {
        if (isInitialized)
            Observable.from(postList)
        else
            fetchPosts()
    }

    fun getNextPosts() {
        fetchPosts()
    }

    private fun fetchPosts() {
        val birthId = sharedPreferencesService.getUserDetails(getApplication())
        isLoading = true
        var hasNewItems = false
        Log.v(TAG, "making a request for posts")

        contentRepository.getNextPosts(token, noOfPosts, lastTimestamp, birthId.birthId)
                .subscribeOn(Schedulers.io())
                .flatMap { nextPosts ->
                    if (!isInitialized)
                        isInitialized = true
                    for (post in nextPosts) {
                        post.votes = post.upVotes - post.downVotes
                        post.setTimeCreatedText()
                        post.setDate()
                    }
                    return@flatMap Observable.from(nextPosts)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            postList.add(it)
                            hasNewItems = true
                        },
                        { it.printStackTrace() },
                        {
                            Log.v(TAG, "oncomplete called " + homeFeedAdapter.postList.size)
                            if (hasNewItems) {
                                val diffUtil = DiffUtil.calculateDiff(
                                        DiffUtilCallback(homeFeedAdapter.postList, postList))
                                postList.sort()
                                homeFeedAdapter.postList.clear()
                                homeFeedAdapter.postList.addAll(postList)
                                lastTimestamp = postList[postList.size - 1].getTimestamp()
                                isLoading = false
                                diffUtil.dispatchUpdatesTo(homeFeedAdapter)
                            }
                        }
                )
        /* .flatMap(
                 { post: Post ->
                     if (post.contentType == "TO")
                         return@flatMap Observable.just(ImageMetadata())
                     else
                         return@flatMap contentRepository.getImageMetadata(token, post._id)
                 }
                 , { post: Post, imageMetadata: ImageMetadata -> post.metadata = imageMetadata;return@flatMap post }
         )*/
    }

    private fun onVoteButtonClick(postId: String, value: Int): Observable<VoteTotal> {
        Log.v(TAG, "upVoteButtonClick")
        val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
        return contentRepository.submitVote(token, postId, birthId, value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())//.map({ it -> return@map it })
    }
}