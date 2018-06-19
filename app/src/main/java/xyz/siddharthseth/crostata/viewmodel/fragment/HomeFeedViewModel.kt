package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.glide.GlideRequests
import xyz.siddharthseth.crostata.data.model.livedata.SingleLivePost
import xyz.siddharthseth.crostata.data.model.livedata.SingleSubject
import xyz.siddharthseth.crostata.data.model.retrofit.LikeTotal
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.diffUtil.PostDiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.FooterListener
import xyz.siddharthseth.crostata.util.recyclerView.PostItemListener
import xyz.siddharthseth.crostata.view.adapter.PostFeedAdapter
import java.util.*
import kotlin.collections.ArrayList


class HomeFeedViewModel(application: Application) : AndroidViewModel(application)
    , PostItemListener, FooterListener {

    override fun reloadSecondary() {
        getNextPosts()
    }

    override fun handleLike(index: Int) {
        if (!isLikeRequestSent) {
            isLikeRequestSent = true

            val post = postList[index]
            val observable = if (post.opinion == 1) clearLike(post) else addLike(post)
            observable.subscribe(
                { isLikeRequestSent = false }
                , { error ->
                    error.printStackTrace()
                    isLikeRequestSent = false
                })

            post.opinion = if (post.opinion == 0) 1 else 0
            post.likes = if (post.opinion == 0) post.likes - 1 else post.likes + 1
            postList[index] = post
            updatePostAdapter()
        }
    }

    override fun addLike(post: Post): Observable<LikeTotal> {
        return contentRepository.submitLike(token, post._id, LoggedSubject.birthId)
            .subscribeOn(Schedulers.io())
    }

    override fun clearLike(post: Post): Observable<LikeTotal> {
        return contentRepository.clearLike(token, post._id, LoggedSubject.birthId)
            .subscribeOn(Schedulers.io())
    }


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

    override fun openFullPost(index: Int) {
        mutablePost.value = postList[index]
    }

    override fun openProfile(index: Int) {
        val post = postList[index]
        mutableSubject.value = Subject(post.creatorId, post.creatorName)
    }

    override fun clearImageGlide(imageView: ImageView) {
        glide.clear(imageView as View)
    }

    override val grey900: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.grey_900))
    override val likeColorTint: ColorStateList
        get() = ColorStateList.valueOf(
            ContextCompat.getColor(
                getApplication(),
                R.color.likeSelected
            )
        )
    override val reportColorTint: ColorStateList
        get() = ColorStateList.valueOf(
            ContextCompat.getColor(
                getApplication(),
                R.color.reportSelected
            )
        )
    override val grey500: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.grey_500))

    private val TAG: String = javaClass.simpleName
    private val sharedPreferencesService = SharedPreferencesService()
    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private var token: String = sharedPreferencesService.getToken(getApplication())
    private var requestId: String = ""
    private var after: Int = 0
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var isInitialized = false
    private var isLikeRequestSent = false
    private var postList: ArrayList<Post> = ArrayList()

    private var isPostRequestSent: Boolean = false
    var isLoadPending = false
    var postFeedAdapter: PostFeedAdapter = PostFeedAdapter(this, this)
    var mutablePost: SingleLivePost = SingleLivePost()
    var mutableSubject: SingleSubject = SingleSubject()
    // var width: Int = 1080
    private var hasNewItems = false
    private var hasReachedEnd = false

    lateinit var glide: GlideRequests

    var mutableLoaderConfig = MutableLiveData<List<Boolean>>()

    init {
        postFeedAdapter.setHasStableIds(true)
        postList.add(Post())
    }

    fun getPosts() {
        setLoaderLiveData(true, true, false, false)
        if (isInitialized) {
            updatePostAdapter()
        } else {
            if (!isPostRequestSent) {
                fetchPosts()
            }
        }
    }

    fun getNextPosts() {
        if (!isPostRequestSent && !hasReachedEnd) {
            setLoaderLiveData(true, true, false, true)
            fetchNextPosts()
        }
    }

    private fun updatePostAdapter() {
        val diffUtil = DiffUtil.calculateDiff(
            PostDiffUtilCallback(postFeedAdapter.postList, postList)
        )

        postFeedAdapter.postList.clear()
        postFeedAdapter.postList = Post.cloneList(postList)

        lastTimestamp = if (postList.size < 2) {
            Calendar.getInstance().timeInMillis
        } else {
            postList[postList.size - 2].getTimestamp()
        }
        after = if (postList.size < 2) {
            0
        } else {
            postList.size
        }
        diffUtil.dispatchUpdatesTo(postFeedAdapter)

        setLoaderLiveData(false, false, false, false)
    }

    private fun fetchPosts() {
        isPostRequestSent = true
        hasNewItems = false

        contentRepository.getNextPosts(token, LoggedSubject.birthId, lastTimestamp)
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
                { onRequestError(it, false) },
                { onRequestCompleted() }
            )
    }

    private fun fetchNextPosts() {
        isPostRequestSent = true
        hasNewItems = false

        contentRepository.getNextPosts(
            token,
            LoggedSubject.birthId,
            lastTimestamp,
            requestId,
            after
        )
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
            .doOnCompleted {
                setLoaderLiveData(false, false, false, true)
            }
            .subscribe(
                { onRequestNext(it) },
                { onRequestError(it, true) },
                { onRequestCompleted() }
            )
    }

    private fun onRequestCompleted() {
        isPostRequestSent = false
        if (hasNewItems) {
            updatePostAdapter()
        } else {
            hasReachedEnd = true
            postFeedAdapter.isEndVisible = true
            postFeedAdapter.notifyItemChanged(postFeedAdapter.postList.size - 1)
        }
    }

    private fun onRequestError(throwable: Throwable, isSecondary: Boolean) {
        throwable.printStackTrace()
        isPostRequestSent = false
        setLoaderLiveData(true, false, true, isSecondary)
    }

    private fun onRequestNext(post: Post) {
        postList.add(if (postList.size == 0) 0 else postList.size - 1, post)
        hasNewItems = true
    }

    private fun setLoaderLiveData(
        isLoaderVisible: Boolean,
        isAnimationVisible: Boolean,
        isErrorVisible: Boolean,
        isSecondary: Boolean
    ) {
        isLoadPending = isLoaderVisible

        val tempList = ArrayList<Boolean>()
        tempList.add(isLoaderVisible)
        tempList.add(isAnimationVisible)
        tempList.add(isErrorVisible)
        if (!isSecondary) {
            mutableLoaderConfig.value = tempList
        } else {
            postFeedAdapter.isErrorVisible = isErrorVisible
            postFeedAdapter.isSecondLoaderVisible = isAnimationVisible
            Handler().post { postFeedAdapter.notifyItemChanged(postList.size - 1) }
        }
    }

    fun refreshData() {
        postList.clear()
        isInitialized = false
        hasNewItems = false
        hasReachedEnd = false
        isPostRequestSent = false
        isLikeRequestSent = false
        lastTimestamp = Calendar.getInstance().timeInMillis

        updatePostAdapter()
        postList.add(Post())

        getPosts()
    }

}