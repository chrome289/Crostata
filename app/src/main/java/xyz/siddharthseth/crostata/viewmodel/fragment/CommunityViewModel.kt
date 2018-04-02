package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.ChartEntry
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    val token = SharedPrefrencesService().getToken(application)
    val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    val TAG: String = javaClass.simpleName
    val birthId = SharedPrefrencesService().getUserDetails(getApplication()).birthId

    fun getChart(): Observable<ChartEntry> {
        return contentRepository.getPatriotChart(token)
                .subscribeOn(Schedulers.io())
                .flatMap { entries -> return@flatMap Observable.from(entries) }
    }

    fun loadProfileImage(birthId2: String, imageView: ImageView,dimension:Int) {
        val context: Context = getApplication()
        val dimen = dimension
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
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .centerInside()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.IMMEDIATE)
                .into(imageView)
    }

    fun getUserInfo(): Observable<Subject> {
        return contentRepository.getSubjectInfo(token, birthId)
                .subscribeOn(Schedulers.io())
    }
}