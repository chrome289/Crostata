package xyz.siddharthseth.crostata.modelView

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.SingleLivePost
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.util.recyclerView.RecyclerViewListener
import java.util.*
import kotlin.collections.ArrayList


class HomeFeedViewModel(application: Application) : AndroidViewModel(application)
        , RecyclerViewListener {

    override fun openFullPost(post: Post) {
        mutablePost.value = post
    }


    override val greyUnselected: Int
        get() = ContextCompat.getColor(getApplication(), R.color.greyUnselected)
    override val upVoteColorTint: Int
        get() = ContextCompat.getColor(getApplication(), R.color.upVoteSelected)
    override val downVoteColorTint: Int
        get() = ContextCompat.getColor(getApplication(), R.color.downVoteSelected)
    override val commentColorTint: Int
        get() = ContextCompat.getColor(getApplication(), R.color.commentSelected)
    override val reportColorTint: Int
        get() = ContextCompat.getColor(getApplication(), R.color.reportSelected)

    private val TAG = "HomeFeedViewModel"
    private val sharedPrefrencesService = SharedPrefrencesService()
    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private var token: String = ""
    private var noOfPosts: Int = 10
    private var lastTimestamp: Float = Calendar.getInstance().timeInMillis / 1000.0f
    var isInitialized = false
    var mutablePost: SingleLivePost = SingleLivePost()
    var postList: ArrayList<Post> = ArrayList<Post>()


//    var fullPost: MutableLiveData<Post> = MutableLiveData<Post>()

    fun getFullPostObservable(): MutableLiveData<Post> {
        return mutablePost
    }

    override fun loadPostedImage(postId: String, imageView: ImageView) {
        val glideUrl = GlideUrl("http://192.168.1.123:3000/api/content/postedImage?post_id=$postId&dimen=1080&quality=80"
                , LazyHeaders.Builder().addHeader("authorization", token).build())
        val context: Context = getApplication()
        GlideApp.with(context)
                .load(glideUrl)//.listener(createLoggerListener("match_image"))
                // .downsample(DownsampleStrategy.CENTER_INSIDE)//.listener(createLoggerListener("match_image"))
                .fitCenter()//.listener(createLoggerListener("match_image"))
                .centerInside()
                // .skipMemoryCache(true)
                // .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .into(imageView)
    }

    override fun loadProfileImage(creatorId: String, imageView: ImageView) {
        val glideUrl = GlideUrl("http://192.168.1.123:3000" + "/api/content/profileImage?birth_id=$creatorId&dimen=256&quality=70"
                , LazyHeaders.Builder().addHeader("authorization", token).build())

        val context: Context = getApplication()
        GlideApp.with(context)
                .load(glideUrl)
                .fitCenter()
                .circleCrop()
                // .skipMemoryCache(true)
                // .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
    }

    /*override fun onUpVoteButtonClick(postId: String): Observable<Boolean> {
        Log.v(TAG, "upVoteButtonClick")
        val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
        return contentRepository.submitVote(token, postId, birthId, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).map({ it -> return@map it.success })
    }*/

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
            return getNextPosts()
    }

    fun getNextPosts(): Observable<Post> {
        // initColorTints()
        token = sharedPrefrencesService.getToken(getApplication())
        val birthId = sharedPrefrencesService.getUserDetails(getApplication())
        return contentRepository.getNextPostsList(token, noOfPosts, lastTimestamp, birthId.birthId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                // .concatMap({ newPosts -> postList.addAll(newPosts.posts);postList.sort();return@concatMap Observable.just(postList) })
                .flatMap({ nextPosts ->
                    if (!isInitialized)
                        isInitialized = true
                    postList.addAll(nextPosts.posts)
                    Log.v(TAG, "postID    " + nextPosts.posts.size)
                    nextPosts.posts.sort()
                    lastTimestamp = nextPosts.posts[nextPosts.posts.size - 1].getTimestamp()
                    return@flatMap Observable.from(nextPosts.posts)
                })
    }

    fun resetLastTimeStamp() {
        lastTimestamp = Float.MAX_VALUE
    }

    /* private fun initColorTints() {
        upVoteColorTint =
        downVoteColorTint = ContextCompat.getColor(getApplication(), R.color.downVoteSelected)
        commentColorTint = ContextCompat.getColor(getApplication(), R.color.commentSelected)
        reportColorTint = ContextCompat.getColor(getApplication(), R.color.reportSelected)
    }*/

    /* fun submitVote(postId: String, birthId: String, value: Int): Observable<VoteTotal> {
         token = sharedPrefrencesService.getToken(getApplication())
         return contentRepository.submitVote(token, postId, birthId, value)
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeOn(Schedulers.io())
     }*/
}
