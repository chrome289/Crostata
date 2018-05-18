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
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.glide.GlideRequests
import xyz.siddharthseth.crostata.data.model.livedata.SingleBirthId
import xyz.siddharthseth.crostata.data.model.livedata.SingleLivePost
import xyz.siddharthseth.crostata.data.model.retrofit.Comment
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.diffUtil.CommentDiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.CommentItemListener
import xyz.siddharthseth.crostata.util.recyclerView.PostItemListener
import xyz.siddharthseth.crostata.view.adapter.ViewPostCommentAdapter
import java.util.*
import kotlin.collections.ArrayList

class ViewPostViewModel(application: Application) : AndroidViewModel(application), CommentItemListener, PostItemListener {

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
        isLoading = true
        return contentRepository.submitComment(token, post._id, LoggedSubject.birthId, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    if (it != null) {
                        it.initExtraInfo(context.getString(R.string.server_url), token)
                        commentList.add(it)
                        updateCommentAdapter()
                        isLoading = false
                        return@flatMap Observable.just(true)
                    } else {
                        isLoading = false
                        Observable.just(false)
                    }
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

    override val unSelectedGrey: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.greyUnselected))
    override val extraDarkGrey: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.extraDarkGrey))
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

    override fun openProfile(birthId: String) {
        Log.v(TAG, "setting birthid")
        mutableBirthId.value = birthId
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
    private var noOfComments: Int = 10
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var isInitialized = false
    private var isLoading = false
    internal var adapter: ViewPostCommentAdapter = ViewPostCommentAdapter(this)
    lateinit var glide: GlideRequests

    internal var mutableBirthId: SingleBirthId = SingleBirthId()
    var mutablePost: SingleLivePost = SingleLivePost()

    init {
        adapter.setHasStableIds(true)
    }

    fun getComments() {
        isLoading = true
        var hasNewItems = false
        val post: Post = mutablePost.value!!
        contentRepository.getComments(token, post._id, noOfComments, lastTimestamp)
                .subscribeOn(Schedulers.io())
                .flatMap { nextComments ->
                    if (!isInitialized)
                        isInitialized = true
                    val context: Context = getApplication()
                    for (comment in nextComments) {
                        comment.initExtraInfo(context.getString(R.string.server_url), token)
                    }
                    return@flatMap Observable.from(nextComments)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ comment: Comment ->
                    commentList.add(comment)
                    hasNewItems = true
                }, { error ->
                    error.printStackTrace()
                    isLoading = false
                }
                        , {
                    Log.v(TAG, "oncomplete called " + adapter.commentList.size)
                    if (hasNewItems) {
                        updateCommentAdapter()
                        isLoading = false
                    }
                })
    }

    private fun updateCommentAdapter() {
        val diffUtil = DiffUtil.calculateDiff(
                CommentDiffUtilCallback(adapter.commentList, commentList))
        commentList.sort()
        adapter.commentList.clear()
        adapter.commentList.addAll(commentList)
        lastTimestamp = commentList[commentList.size - 1].getTimestamp()
        diffUtil.dispatchUpdatesTo(adapter)
    }

    fun initPost(post: Post) {
        val context: Context = getApplication()
        post.initExtraInfo(context.getString(R.string.server_url), token)
        mutablePost.value = post
    }
}