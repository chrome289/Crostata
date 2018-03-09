package xyz.siddharthseth.crostata.modelView

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.util.recyclerView.RecyclerViewListener
import java.util.*

class HomeFeedViewModel(application: Application) : AndroidViewModel(application)
        , RecyclerViewListener {
    private val _tag = "HomeFeedViewModel"
    private val sharedPrefrencesService = SharedPrefrencesService()
    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private var token: String = ""
    private var noOfPosts: Int = 10
    private var lastTimestamp: Float = Calendar.getInstance().timeInMillis / 1000.0f


    override fun loadPostedImage(postId: String, imageView: ImageView) {
        val glideUrl = GlideUrl("http://192.168.1.123:3000/api/content/postedImage?post_id=$postId"
                , LazyHeaders.Builder().addHeader("authorization", token).build())
        val context: Context = getApplication()
        GlideApp.with(context)
                .load(glideUrl)
                .fitCenter()
                .centerInside()
                //.skipMemoryCache(true)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
    }

    override fun loadProfileImage(creatorId: String, imageView: ImageView) {
        val glideUrl = GlideUrl("http://192.168.1.123:3000" + "/api/content/profileImage?birth_id=" + creatorId
                + "&dimen=256&quality=80"
                , LazyHeaders.Builder().addHeader("authorization", token).build())

        val context: Context = getApplication()
        GlideApp.with(context)
                .load(glideUrl)
                .fitCenter()
                .circleCrop()
                //.skipMemoryCache(true)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
    }

    /*override fun onUpVoteButtonClick(postId: String): Observable<Boolean> {
        Log.v(_tag, "upVoteButtonClick")
        val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
        return contentRepository.submitVote(token, postId, birthId, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).map({ it -> return@map it.success })
    }*/

    override fun onVoteButtonClick(postId: String, value: Int): Observable<VoteTotal> {
        Log.v(_tag, "upVoteButtonClick")
        val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
        return contentRepository.submitVote(token, postId, birthId, value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).map({ it -> return@map it })
    }

    override fun onClearVote(postId: String): Observable<VoteTotal> {
        Log.v(_tag, "upVoteButtonClick")
        val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
        return contentRepository.clearVote(token, postId, birthId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).map({ it -> return@map it })
    }

    override fun onCommentButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReportButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getNextPosts(): Observable<ArrayList<Post>> {
        token = sharedPrefrencesService.getToken(getApplication())
        val birthId = sharedPrefrencesService.getUserDetails(getApplication())
        return contentRepository.getNextPostsList(token, noOfPosts, lastTimestamp, birthId.birthId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .concatMap({ nextPosts ->
                    Log.v(_tag, "postID    " + nextPosts.posts.size)
                    nextPosts.posts.sort()
                    lastTimestamp = nextPosts.posts[nextPosts.posts.size - 1].getTimestamp()
                    return@concatMap Observable.just(nextPosts.posts)
                })
    }

    /* fun submitVote(postId: String, birthId: String, value: Int): Observable<VoteTotal> {
         token = sharedPrefrencesService.getToken(getApplication())
         return contentRepository.submitVote(token, postId, birthId, value)
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeOn(Schedulers.io())
     }*/
}
