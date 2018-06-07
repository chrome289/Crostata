package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
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
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.glide.GlideRequests
import xyz.siddharthseth.crostata.data.model.livedata.SingleLivePost
import xyz.siddharthseth.crostata.data.model.livedata.SingleSubject
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.diffUtil.PostDiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.PostItemListener
import xyz.siddharthseth.crostata.view.adapter.HomeFeedAdapter
import java.util.*
import kotlin.collections.ArrayList

class ProfileViewModel(application: Application) : AndroidViewModel(application), PostItemListener {

    override fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView) {
        glide.load(post.glideUrl)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .thumbnail(glide.load(post.glideUrlThumb).priority(Priority.IMMEDIATE).centerCrop())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(imageView)
    }

    override fun loadProfileImage(post: Post, dimen: Int, imageView: ImageView) {
        glide.load(post.glideUrlProfileThumb)
                .priority(Priority.LOW)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(imageView)
    }

    override fun handleVote(post: Post, value: Int) {
        if (post.opinion == value) {
            onClearVote(post._id)
                    .subscribe(
                            { voteTotal: VoteTotal ->
                                Log.v(TAG, "success :" + voteTotal.success)
                                if (voteTotal.success) {
                                    post.opinion = 0
                                    post.votes = voteTotal.total
                                    // updatePostItem(post)
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
                                    // updatePostItem(post)
                                }
                            }
                            , { error -> error.printStackTrace() })
        }
    }

    override fun openFullPost(index: Int) {
        Log.v(TAG, "viewmodel click listener")
        mutablePost.value = postList[index]
    }

    override fun clearPostedImageGlide(imageView: ImageView) {
        val context: Context = getApplication()
        GlideApp.with(context).clear(imageView as View)
    }

    override fun onCommentButtonClick(comment: String): Observable<Boolean> {
        return Observable.empty()
    }

    override fun onReportButtonClick(post: Post) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClearVote(postId: String): Observable<VoteTotal> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val extraDarkGrey: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.grey_900))
    override val upVoteColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.upVoteSelected))
    override val downVoteColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.downVoteSelected))
    override val reportColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.reportSelected))
    override val grey500: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.grey_500))

    override fun openProfile(index: Int) {
        Log.v(TAG, "setting birthid")
        val post = postList[index]
        mutableSubject.value = Subject(post.creatorId, post.creatorName)
    }

    private val token = SharedPreferencesService().getToken(application)
    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val TAG: String = javaClass.simpleName
    lateinit var birthId: String
    private var requestId: String = ""
    private var after: Int = 0
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var postList = ArrayList<Post>()
    private var isInitialized = false
    val mutablePost: SingleLivePost = SingleLivePost()
    private var mutableSubject: SingleSubject = SingleSubject()
    lateinit var glide: GlideRequests

    var profilePostAdapter: HomeFeedAdapter = HomeFeedAdapter(this)
    private var hasNewItems = false
    private var isPostRequestSent: Boolean = false
    var isVoteRequestSent = false
    var isLoadPending = false
    var mutableLoaderConfig = MutableLiveData<List<Boolean>>()

    init {
        profilePostAdapter.setHasStableIds(true)
    }

    fun loadOwnProfileImage(dimen: Int, imageView: ImageView) {
        val context: Context = getApplication()
        val quality = 80
        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "subject/profileImage?birthId=$birthId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())
        GlideApp.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.IMMEDIATE)
                .into(imageView)
    }

    fun getInfo(): Observable<Subject> {
        return contentRepository.getSubjectInfo(token, birthId)
                .subscribeOn(Schedulers.io())
    }

    fun getPosts() {
        setLoaderLiveData(true, true, false)
        if (isInitialized)
            updatePostAdapter()
        else
            fetchPosts()
    }

    fun getMorePosts() {
        fetchNextPosts()
    }

    private fun fetchPosts() {
        if (!isPostRequestSent) {
            isPostRequestSent = true
            hasNewItems = false

            contentRepository.getSubjectPosts(token, birthId, LoggedSubject.birthId, lastTimestamp)
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        if (!isInitialized)
                            isInitialized = true
                        val context: Context = getApplication()
                        for (post in it.list) {
                            post.initExtraInfo(context.getString(R.string.server_url), token)
                        }
                        requestId = it.requestId
                        Observable.from(it.list)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { onRequestNext(it) },
                            { onRequestError(it) },
                            { onRequestComplete() }
                    )
        }
    }

    private fun fetchNextPosts() {
        if (!isPostRequestSent) {
            isPostRequestSent = true
            hasNewItems = false

            contentRepository.getSubjectPosts(token, birthId, LoggedSubject.birthId, lastTimestamp, requestId, after)
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        if (!isInitialized)
                            isInitialized = true
                        val context: Context = getApplication()
                        for (post in it.list) {
                            post.initExtraInfo(context.getString(R.string.server_url), token)
                        }
                        Observable.from(it.list)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { onRequestNext(it) },
                            { onRequestError(it) },
                            { onRequestComplete() }
                    )
        }
    }

    private fun onRequestComplete() {
        isPostRequestSent = false
        if (hasNewItems) {
            updatePostAdapter()
        }
    }

    private fun onRequestError(throwable: Throwable) {
        isPostRequestSent = false
        setLoaderLiveData(true, false, true)
        throwable.printStackTrace()
    }

    private fun onRequestNext(post: Post) {
        postList.add(post)
        hasNewItems = true
    }

    private fun updatePostAdapter() {
        val diffUtil = DiffUtil.calculateDiff(
                PostDiffUtilCallback(profilePostAdapter.postList, postList))
        postList.sort()
        profilePostAdapter.postList.clear()
        profilePostAdapter.postList.addAll(postList)
        lastTimestamp = if (postList.isEmpty()) {
            Calendar.getInstance().timeInMillis
        } else {
            postList[postList.size - 1].getTimestamp()
        }
        after = if (postList.isEmpty()) {
            0
        } else {
            postList.size
        }
        diffUtil.dispatchUpdatesTo(profilePostAdapter)

        setLoaderLiveData(false, false, false)
        Log.v(TAG, "onScroll 5")
    }

    private fun onVoteButtonClick(postId: String, value: Int): Observable<VoteTotal> {
        Log.v(TAG, "upVoteButtonClick")
        //val birthId = SharedPreferencesService().getUserDetails(getApplication()).birthId
        return if (!isVoteRequestSent) {
            isVoteRequestSent = true
            contentRepository.submitVote(token, postId, birthId, value)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { isVoteRequestSent = false }
                    .doOnError { isVoteRequestSent = false }
                    .subscribeOn(Schedulers.io())//.map({ it -> return@map it })
        } else {
            Observable.empty()
        }
    }

    private fun setLoaderLiveData(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean) {
        Log.d(TAG, "setLoaderVisibility $isLoaderVisible $isAnimationVisible $isErrorVisible")
        isLoadPending = isLoaderVisible

        val tempList = ArrayList<Boolean>()
        tempList.add(isLoaderVisible)
        tempList.add(isAnimationVisible)
        tempList.add(isErrorVisible)
        mutableLoaderConfig.value = tempList
    }

    fun refreshData() {
        profilePostAdapter.postList.clear()
        postList.clear()

        isInitialized = false
        lastTimestamp = Calendar.getInstance().timeInMillis

        updatePostAdapter()

        getPosts()
    }
}