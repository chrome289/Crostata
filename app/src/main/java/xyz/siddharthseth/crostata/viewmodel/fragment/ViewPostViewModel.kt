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
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.glide.GlideRequests
import xyz.siddharthseth.crostata.data.model.livedata.SingleLivePost
import xyz.siddharthseth.crostata.data.model.livedata.SingleSubject
import xyz.siddharthseth.crostata.data.model.retrofit.Comment
import xyz.siddharthseth.crostata.data.model.retrofit.LikeTotal
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.diffUtil.CommentDiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.CommentItemListener
import xyz.siddharthseth.crostata.util.recyclerView.FooterListener
import xyz.siddharthseth.crostata.util.recyclerView.PostItemListener
import xyz.siddharthseth.crostata.view.adapter.ViewPostCommentAdapter
import java.util.*
import kotlin.collections.ArrayList

class ViewPostViewModel(application: Application) : AndroidViewModel(application), CommentItemListener, PostItemListener, FooterListener {
    override fun reloadSecondary() {
        getNextComments()
    }

    fun handleLike() {
        val post = mutablePost.value
        if (post != null) {
            if (!isLikeRequestSent) {
                isLikeRequestSent = true
                val observable = if (post.opinion == 1) clearLike(post) else addLike(post)
                observable.subscribe(
                        {
                            isLikeRequestSent = false
                        }
                        , { error ->
                    error.printStackTrace()
                    isLikeRequestSent = false
                })

                post.opinion = if (post.opinion == 0) 1 else 0
                post.likes = if (post.opinion == 0) post.likes - 1 else post.likes + 1
                mutablePost.value = post
            }
        }
    }

    override fun handleLike(index: Int) {
        //not needed
    }

    override fun openFullPost(index: Int) {
        //not needed
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
                .thumbnail(glide.load(post.glideUrlThumb).priority(Priority.IMMEDIATE).fitCenter())
                .fitCenter()
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

    override fun loadProfileImage(comment: Comment, dimen: Int, imageView: ImageView) {
        glide.load(comment.glideUrlProfileThumb)
                .priority(Priority.LOW)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(imageView)
    }

    override fun onCommentButtonClick(comment: String): Observable<Boolean> {
        val post: Post = mutablePost.value!!
        val context: Context = getApplication()
        return if (!isSubmitCommentRequestSent) {
            isSubmitCommentRequestSent = true
            contentRepository.submitComment(token, post._id, LoggedSubject.birthId, comment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        if (it != null) {
                            it.initExtraInfo(context.getString(R.string.server_url), token)
                            commentList.add(it)
                            updateCommentAdapter()
                            isSubmitCommentRequestSent = false
                            return@flatMap Observable.just(true)
                        } else {
                            isSubmitCommentRequestSent = false
                            Observable.just(false)
                        }
                    }
        } else {
            Observable.empty()
        }
    }

    override fun clearPostedImageGlide(imageView: ImageView) {
        val context: Context = getApplication()
        GlideApp.with(context).clear(imageView as View)
    }

    override val grey500: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.grey_500))
    override val grey900: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.grey_900))
    override val likeColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.likeSelected))
    override val reportColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.reportSelected))

    override fun onReportButtonClick(index: Int): Observable<Boolean> {
        val comment = commentList[index]
        if (!isReportRequestSent) {
            isReportRequestSent = true
            return contentRepository.submitReport(token, comment.birthId, LoggedSubject.birthId, 1, comment._id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        isReportRequestSent = false
                    }
                    .doOnError {
                        isReportRequestSent = false
                        it.printStackTrace()
                    }
                    .flatMap {
                        if (it.isSuccessful) Observable.just(true) else Observable.just(false)
                    }
        } else {
            return Observable.empty()
        }
    }

    override fun openProfile(index: Int) {
        Log.v(TAG, "setting birthid")
        val comment = commentList[index]
        mutableSubject.value = Subject(comment.birthId, comment.name)
    }

    private var commentList = ArrayList<Comment>()
    private val sharedPreferencesService = SharedPreferencesService()
    private var token: String = sharedPreferencesService.getToken(getApplication())
    private val TAG: String = javaClass.simpleName
    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private var after: Int = 0
    private var requestId: String = ""
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var isInitialized = false
    internal var adapter: ViewPostCommentAdapter = ViewPostCommentAdapter(this, this)
    lateinit var glide: GlideRequests
    private var isCommentRequestSent = false
    private var isSubmitCommentRequestSent = false
    private var isLikeRequestSent = false
    private var isReportRequestSent = false
    private var hasNewItems = false
    private var hasReachedEnd = false
    private var isLoadPending = false

    internal var mutableSubject: SingleSubject = SingleSubject()
    var mutablePost: SingleLivePost = SingleLivePost()

    init {
        adapter.setHasStableIds(true)
        commentList.add(Comment())
    }

    fun getComments() {
        setLoaderLiveData(true, true, false)
        if (isInitialized) {
            updateCommentAdapter()
        } else {
            if (!isCommentRequestSent) {
                fetchComments()
            }
        }
    }

    fun getNextComments() {
        if (!isCommentRequestSent && !hasReachedEnd) {
            setLoaderLiveData(true, true, false)
            fetchNextComments()
        }
    }

    private fun fetchComments() {
        hasNewItems = false
        isCommentRequestSent = true
        val post: Post = mutablePost.value!!
        contentRepository.getComments(token, post._id, lastTimestamp)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    if (!isInitialized)
                        isInitialized = true
                    val context: Context = getApplication()
                    for (comment in it.list) {
                        comment.initExtraInfo(context.getString(R.string.server_url), token)
                    }
                    requestId = it.requestId
                    return@flatMap Observable.from(it.list)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onRequestNext(it) }
                        , { onRequestError(it) }
                        , { onRequestCompleted() }
                )
    }

    private fun onRequestCompleted() {
        if (hasNewItems) {
            isCommentRequestSent = false
            updateCommentAdapter()
        } else {
            hasReachedEnd = true
            adapter.isEndVisible = true
            adapter.notifyItemChanged(adapter.commentList.size - 1)
        }
    }

    private fun onRequestError(throwable: Throwable) {
        isCommentRequestSent = false
        setLoaderLiveData(true, false, true)
        throwable.printStackTrace()
    }

    private fun onRequestNext(comment: Comment) {
        hasNewItems = true
        commentList.add(if (commentList.size == 0) 0 else commentList.size - 1, comment)
    }

    private fun fetchNextComments() {
        hasNewItems = false
        isCommentRequestSent = true
        val post: Post = mutablePost.value!!
        contentRepository.getComments(token, post._id, lastTimestamp, requestId, after)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    if (!isInitialized)
                        isInitialized = true
                    val context: Context = getApplication()
                    for (comment in it.list) {
                        comment.initExtraInfo(context.getString(R.string.server_url), token)
                    }
                    return@flatMap Observable.from(it.list)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted {
                    setLoaderLiveData(false, false, false)
                }
                .subscribe({ onRequestNext(it) }
                        , { onRequestError(it) }
                        , { onRequestCompleted() }
                )
    }


    private fun updateCommentAdapter() {
        val diffUtil = DiffUtil.calculateDiff(
                CommentDiffUtilCallback(adapter.commentList, commentList))
        adapter.commentList.clear()
        adapter.commentList = Comment.cloneList(commentList)
        lastTimestamp = if (commentList.size < 2) {
            Calendar.getInstance().timeInMillis
        } else {
            commentList[commentList.size - 2].getTimestamp()
        }
        after = if (commentList.size < 2) {
            0
        } else {
            commentList.size
        }
        diffUtil.dispatchUpdatesTo(adapter)

        setLoaderLiveData(false, false, false)
    }

    fun initPost(post: Post) {
        val context: Context = getApplication()
        post.initExtraInfo(context.getString(R.string.server_url), token)
        mutablePost.value = post
    }

    fun loadProfileImage(dimen: Int, profileImage: ImageView) {
        val context: Context = getApplication()
        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "subject/profileImage?birthId=${LoggedSubject.birthId}&dimen=$dimen&quality=80"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        glide.load(glideUrl)
                .priority(Priority.LOW)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(profileImage)
    }

    fun refreshComments() {
        commentList.clear()
        commentList.add(Comment())

        isInitialized = false
        hasNewItems = false
        hasReachedEnd = false
        isCommentRequestSent = false
        isSubmitCommentRequestSent = false
        isReportRequestSent = false
        isLikeRequestSent = false
        isLoadPending = false
        lastTimestamp = Calendar.getInstance().timeInMillis

        updateCommentAdapter()

        getComments()
    }

    fun onReportButtonClick(): Observable<Boolean> {
        val post = mutablePost.value
        if (post != null && !isReportRequestSent) {
            isReportRequestSent = true
            return contentRepository.submitReport(token, post.creatorId, LoggedSubject.birthId, 0, post._id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        isReportRequestSent = false
                    }
                    .doOnError {
                        isReportRequestSent = false
                        it.printStackTrace()
                    }
                    .flatMap {
                        if (it.isSuccessful)
                            Observable.just(true)
                        else
                            Observable.just(false)
                    }
        } else {
            return Observable.empty()
        }
    }

    private fun setLoaderLiveData(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean) {
        isLoadPending = isLoaderVisible

        val tempList = ArrayList<Boolean>()
        tempList.add(isLoaderVisible)
        tempList.add(isAnimationVisible)
        tempList.add(isErrorVisible)
        adapter.isErrorVisible = isErrorVisible
        adapter.isSecondLoaderVisible = isAnimationVisible
        adapter.notifyItemChanged(commentList.size - 1)
    }


    fun openProfile(birthId: String, name: String) {
        mutableSubject.value = Subject(birthId, name)
    }
}
