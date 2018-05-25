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
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.glide.GlideRequests
import xyz.siddharthseth.crostata.data.model.livedata.SingleBirthId
import xyz.siddharthseth.crostata.data.model.livedata.SingleLivePost
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.diffUtil.PostDiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.PostItemListener
import xyz.siddharthseth.crostata.view.adapter.HomeFeedAdapter
import java.util.*
import kotlin.collections.ArrayList


class HomeFeedViewModel(application: Application) : AndroidViewModel(application)
        , PostItemListener {

    override fun handleVote(post: Post, value: Int) {}

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
        Log.v(TAG, "viewmodel click listener")
        mutablePost.value = postList[index]
    }

    override fun openProfile(birthId: String) {
        Log.v(TAG, "setting birthid $birthId")
        mutableBirthId.value = birthId
    }

    override fun clearPostedImageGlide(imageView: ImageView) {
        glide.clear(imageView as View)
    }

    override fun onClearVote(postId: String): Observable<VoteTotal> {
        return Observable.empty()
    }

    override fun onCommentButtonClick(comment: String): Observable<Boolean> {
        return Observable.empty()
    }

    override fun onReportButtonClick(post: Post) {}

    override val extraDarkGrey: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.extraDarkGrey))
    override val upVoteColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.upVoteSelected))
    override val downVoteColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.downVoteSelected))
    override val reportColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.reportSelected))
    override val unSelectedGrey: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.greyUnselected))

    private val TAG = javaClass.simpleName
    private val sharedPreferencesService = SharedPreferencesService()
    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private var token: String = sharedPreferencesService.getToken(getApplication())
    private var noOfPosts: Int = 10
    private var lastTimestamp: Long = Calendar.getInstance().timeInMillis
    private var isInitialized = false
    private var postList: ArrayList<Post> = ArrayList()

    var isPostRequestSent: Boolean = false
    var isLoadPending = false
    var homeFeedAdapter: HomeFeedAdapter = HomeFeedAdapter(this)
    var mutablePost: SingleLivePost = SingleLivePost()
    var mutableBirthId: SingleBirthId = SingleBirthId()
    // var width: Int = 1080
    private var hasNewItems = false

    lateinit var glide: GlideRequests

    var mutableLoaderConfig = MutableLiveData<List<Boolean>>()

    init {
        homeFeedAdapter.setHasStableIds(true)
    }

    fun getPosts() {
        setLoaderLiveData(true, true, false)
        if (isInitialized) {
            updatePostAdapter()
        } else
            fetchPosts()
    }

    fun getNextPosts() {
        fetchPosts()
    }

    private fun updatePostAdapter() {
        val diffUtil = DiffUtil.calculateDiff(
                PostDiffUtilCallback(homeFeedAdapter.postList, postList))
        postList.sort()
        homeFeedAdapter.postList.clear()
        homeFeedAdapter.postList.addAll(postList)
        if (postList.isEmpty()) {
            lastTimestamp = Calendar.getInstance().timeInMillis
        } else {
            lastTimestamp = postList[postList.size - 1].getTimestamp()
        }
        diffUtil.dispatchUpdatesTo(homeFeedAdapter)

        setLoaderLiveData(false, false, false)
    }

    private fun fetchPosts() {
        // val birthId = sharedPreferencesService.getUserDetails(getApplication())
        if (!isPostRequestSent) {
            isPostRequestSent = true
            hasNewItems = false
            Log.v(TAG, "making a request for posts")

            contentRepository.getNextPosts(token, noOfPosts, lastTimestamp, LoggedSubject.birthId)
                    .subscribeOn(Schedulers.io())
                    .flatMap { nextPosts ->
                        if (!isInitialized)
                            isInitialized = true
                        val context: Context = getApplication()
                        for (post in nextPosts) {
                            post.initExtraInfo(context.getString(R.string.server_url), token)
                        }
                        return@flatMap Observable.from(nextPosts)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                postList.add(it)
                                hasNewItems = true
                            },
                            {
                                it.printStackTrace()
                                isPostRequestSent = false
                                setLoaderLiveData(true, false, true)
                            },
                            {
                                isPostRequestSent = false
                                Log.v(TAG, "oncomplete called " + homeFeedAdapter.postList.size)
                                if (hasNewItems) {
                                    updatePostAdapter()
                                }
                            }
                    )
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
        homeFeedAdapter.postList.clear()
        postList.clear()

        isInitialized = false
        lastTimestamp = Calendar.getInstance().timeInMillis

        updatePostAdapter()

        getPosts()
    }

}