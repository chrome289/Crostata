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
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.diffUtil.CommentDiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.CommentItemListener
import xyz.siddharthseth.crostata.util.recyclerView.PostItemListener
import xyz.siddharthseth.crostata.util.viewModel.ViewPostInteractionListener
import xyz.siddharthseth.crostata.view.adapter.ViewPostCommentAdapter
import java.util.*
import kotlin.collections.ArrayList

class ViewPostViewModel(application: Application) : AndroidViewModel(application), CommentItemListener, PostItemListener, ViewPostInteractionListener {

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

    override fun onClearVote(postId: String): Observable<VoteTotal> {
        Log.v(TAG, "upVoteButtonClick")
        val birthId = LoggedSubject.birthId
        return contentRepository.clearVote(token, postId, birthId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun clearPostedImageGlide(imageView: ImageView) {
        val context: Context = getApplication()
        GlideApp.with(context).clear(imageView as View)
    }

    override val grey500: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.grey_500))
    override val extraDarkGrey: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.grey_900))
    override val upVoteColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.upVoteSelected))
    override val downVoteColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.downVoteSelected))
    override val reportColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.reportSelected))

    override fun openFullPost(index: Int) {
        //nah
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
                                    updatePostItem(post)
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
                                    updatePostItem(post)
                                }
                            }
                            , { error -> error.printStackTrace() })
        }
    }

    override fun onReportButtonClick(post: Post) {}

    override fun openProfile(birthId: String, name: String) {
        mutableSubject.value = Subject(birthId, name)
    }

    override fun openProfile(index: Int) {
        Log.v(TAG, "setting birthid")
        val comment = commentList[index]
        mutableSubject.value = Subject(comment.birthId, comment.name)
    }

    private fun updatePostItem(post: Post) {
        mutablePost.value = post
    }

    private fun onVoteButtonClick(postId: String, value: Int): Observable<VoteTotal> {
        Log.v(TAG, "upVoteButtonClick")
        return contentRepository.submitVote(token, postId, LoggedSubject.birthId, value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
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
    internal var adapter: ViewPostCommentAdapter = ViewPostCommentAdapter(this)
    lateinit var glide: GlideRequests
    private var isCommentRequestSent = false
    private var isSubmitCommentRequestSent = false
    var hasNewItems = false

    internal var mutableSubject: SingleSubject = SingleSubject()
    var mutablePost: SingleLivePost = SingleLivePost()

    init {
        adapter.setHasStableIds(true)
    }

    fun getComments() {
        //setLoaderLiveData(true, true, false)
        if (isInitialized) {
            updateCommentAdapter()
        } else
            fetchComments()

    }

    fun getNextComments() {
        fetchNextComments()
    }

    private fun fetchComments() {
        if (!isCommentRequestSent) {
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
    }

    private fun onRequestCompleted() {
        if (hasNewItems) {
            isCommentRequestSent = false
            updateCommentAdapter()
        }
    }

    private fun onRequestError(throwable: Throwable) {
        isCommentRequestSent = false
        throwable.printStackTrace()
    }

    private fun onRequestNext(comment: Comment) {
        commentList.add(comment)
        hasNewItems = true
    }

    private fun fetchNextComments() {
        if (!isCommentRequestSent) {
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
                    .subscribe({ onRequestNext(it) }
                            , { onRequestError(it) }
                            , { onRequestCompleted() }
                    )
        }
    }

    private fun updateCommentAdapter() {
        val diffUtil = DiffUtil.calculateDiff(
                CommentDiffUtilCallback(adapter.commentList, commentList))
        commentList.sort()
        adapter.commentList.clear()
        adapter.commentList.addAll(commentList)
        lastTimestamp = if (commentList.isEmpty()) {
            Calendar.getInstance().timeInMillis
        } else {
            commentList[commentList.size - 1].getTimestamp()
        }
        after = if (commentList.isEmpty()) {
            0
        } else {
            commentList.size
        }
        diffUtil.dispatchUpdatesTo(adapter)
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
        adapter.commentList.clear()
        commentList.clear()

        isInitialized = false
        lastTimestamp = Calendar.getInstance().timeInMillis

        updateCommentAdapter()

        getComments()
    }
}