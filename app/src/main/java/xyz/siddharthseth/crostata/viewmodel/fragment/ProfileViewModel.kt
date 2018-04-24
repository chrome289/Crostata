package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
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
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.livedata.SingleBirthId
import xyz.siddharthseth.crostata.data.model.livedata.SingleLivePost
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.util.recyclerView.listeners.PostItemListener
import java.util.*
import kotlin.collections.ArrayList

class ProfileViewModel(application: Application) : AndroidViewModel(application), PostItemListener {

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

    override fun openFullPost(index: Int) {
        Log.v(TAG, "viewmodel click listener")
        mutablePost.value = postList[index]
    }

    override fun loadProfileImage(creatorId: String, dimen: Int, isCircle: Boolean, imageView: ImageView) {
        val context: Context = getApplication()
        val quality = 70
        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/subject/profileImage?birthId=$birthId&dimen=$dimen&quality=$quality"
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

    override fun clearPostedImageGlide(imageView: ImageView) {
        val context: Context = getApplication()
        GlideApp.with(context).clear(imageView as View)
    }

    override fun loadPostedImage(post: Post, dimen: Int, imageView: ImageView) {
        val context: Context = getApplication()

        val quality = 70
        val imageId = post.imageId

        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/content/postedImage?imageId=$imageId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        // Log.v(TAG, "token-$token")
        GlideApp.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.palmyra)
                .fitCenter()
                .override(1080)
                .into(imageView)
    }

    override fun onCommentButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReportButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClearVote(postId: String): Observable<VoteTotal> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val extraDarkGrey: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.extraDarkGrey))
    override val voteColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.voteSelected))
    override val reportColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.reportSelected))

    override fun openProfile(birthId: String) {
        Log.v(TAG, "setting birthid")
        mutableBirthId.value = birthId
    }

    private val token = SharedPrefrencesService().getToken(application)
    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val TAG: String = javaClass.simpleName
    lateinit var birthId: String
    private var size: Int = 5
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var commentList = ArrayList<Comment>()
    private var postList = ArrayList<Post>()
    private var isInitialized = false
    val mutablePost: SingleLivePost = SingleLivePost()
    private var mutableBirthId: SingleBirthId = SingleBirthId()

    fun getInfo(): Observable<Subject> {
        return contentRepository.getSubjectInfo(token, birthId)
                .subscribeOn(Schedulers.io())
    }

    fun getComments(): Observable<Comment> {
        return if (isInitialized)
            Observable.from(commentList)
        else
            getNextComments()
    }


    private fun getNextComments(): Observable<Comment> {
        return contentRepository.getSubjectComments(token, birthId, size, lastTimestamp)
                .subscribeOn(Schedulers.io())
                .flatMap { return@flatMap Observable.from(it) }
    }


    fun getPosts(): Observable<Post> {
        return if (isInitialized)
            Observable.from(postList)
        else
            getNextPosts()
    }

    private fun getNextPosts(): Observable<Post> {
        return contentRepository.getSubjectPosts(token, birthId, size, lastTimestamp)
                .subscribeOn(Schedulers.io())
                .flatMap { return@flatMap Observable.from(it) }
    }

    private fun updatePostItem(post: Post, index: Int) {
        postList[index] = post
        // val diffUtil = DiffUtil.calculateDiff(DiffUtilCallback(homeFeedAdapter.postList, postList))
        //diffUtil.dispatchUpdatesTo(homeFeedAdapter)
    }

    private fun onVoteButtonClick(postId: String, value: Int): Observable<VoteTotal> {
        Log.v(TAG, "upVoteButtonClick")
        val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
        return contentRepository.submitVote(token, postId, birthId, value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())//.map({ it -> return@map it })
    }
}