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
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.view.adapter.viewholder.AdapterPost
import java.util.*

class HomeFeedViewModel(application: Application) : AndroidViewModel(application)
        , AdapterPost.OnRecyclerViewEventListener {

    override fun loadPostedImage(postId: String, imageView: ImageView) {
        val glideUrl = GlideUrl("http://192.168.1.123:3000" + "/api/content/postedImage?post_id=" + postId
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
        //TODO shift this code to modelview
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

    private val TAG = "HomeFeedViewModel"
    private val sharedPrefrencesService = SharedPrefrencesService()
    val contentRepository = ContentRepositoryProvider.getContentRepository()
    var token: String = ""
    var noOfPosts: Int = 10
    var lastTimestamp: Float = Calendar.getInstance().timeInMillis / 1000.0f

    override fun onVoteButtonClick(postId: String) {
        Log.v(TAG, "adsfsdfwefv2232323")
    }

    override fun onDownButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCommentButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReportButtonClick(postId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getNextPosts(): Observable<ArrayList<Post>> {
        token = sharedPrefrencesService.getToken(getApplication())
        return contentRepository.getNextPostsList(token, noOfPosts, lastTimestamp)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .concatMap({ nextPosts ->
                    Log.v(TAG, "postID    " + nextPosts.posts.size)
                    nextPosts.posts.sort()
                    lastTimestamp = nextPosts.posts.get(nextPosts.posts.size - 1).getTimestamp()
                    return@concatMap Observable.just(nextPosts.posts)
                })
    }

    fun submitVote(postId: String, birthId: String, value: Int): Observable<Boolean> {
        token = sharedPrefrencesService.getToken(getApplication())
        return contentRepository.submitVote(token, postId, birthId, value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }
}
