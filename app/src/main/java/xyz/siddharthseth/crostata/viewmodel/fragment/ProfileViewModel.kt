package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.NextComments
import xyz.siddharthseth.crostata.data.model.retrofit.NextPosts
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import java.util.*
import kotlin.collections.ArrayList

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val token = SharedPrefrencesService().getToken(application)
    val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    val TAG: String = javaClass.simpleName
    val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId
    private var noOfPosts: Int = 5
    private var noOfComments: Int = 5
    private var lastPostTimestamp: Float = Calendar.getInstance().timeInMillis / 1000.0f
    private var lastCommentTimestamp: Float = Calendar.getInstance().timeInMillis / 1000.0f

    var commentList = ArrayList<Comment>()
    var postList = ArrayList<Post>()
    private var isInitialized = false

    fun getInfo(): Observable<Subject> {
        return contentRepository.getSubjectInfo(token, birthId)
                .subscribeOn(Schedulers.io())
    }

    fun loadProfileImage(imageView: ImageView) {
        val context: Context = getApplication()
        val dimen = 1024
        val quality = 70
        //TODO birthid hardcoded
        val glideUrl = GlideUrl(context.getString(R.string.server_url) +
                "/api/subject/profileImage?birthId=062912952&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())

        GlideApp.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.IMMEDIATE)
                .into(imageView)
    }

    fun getComments(): Observable<Comment> {
        if (isInitialized)
            return Observable.from(commentList)
        else
            return getNextComments()
    }

    fun getNextComments(): Observable<Comment> {
        return contentRepository.getSubjectComments(token, birthId, noOfComments, lastCommentTimestamp)
                .subscribeOn(Schedulers.io())
                .flatMap({ nextComments: NextComments ->
                    //Thread.sleep(5000)
                    if (!isInitialized)
                        isInitialized = true
                    commentList.addAll(nextComments.comments)
                    commentList.sort()
                    lastCommentTimestamp = commentList[commentList.size - 1].getTimestamp()
                    return@flatMap Observable.from(nextComments.comments)
                })
    }
}