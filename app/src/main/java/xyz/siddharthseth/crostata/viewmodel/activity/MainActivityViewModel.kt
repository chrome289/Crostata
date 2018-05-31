package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val token = SharedPreferencesService().getToken(application)
    private val TAG: String = this::class.java.simpleName
    var isDetailActivityOpen: Boolean = false
    var subject: Subject = Subject(LoggedSubject.birthId, "")
    var isServerStatusRequestSent = false
    var isPiRequestSent = false

    internal fun getToolbarTitle(fragmentId: Int): String {
        val context: Context = getApplication()
        return when (fragmentId) {
            R.id.community -> context.resources.getString(R.string.toolbar_community)
            R.id.vigilance -> context.resources.getString(R.string.toolbar_vigilance)
            R.id.viewPost -> context.resources.getString(R.string.comments)
            else -> context.resources.getString(R.string.toolbar_home)
        }
    }

    fun checkNetworkAvailable(): Observable<Boolean> {
        return if (!isServerStatusRequestSent) {
            isServerStatusRequestSent = true
            contentRepository.serverStatus(token)
                    .subscribeOn(Schedulers.io())
                    .doOnNext { isServerStatusRequestSent = false }
                    .doOnError { isServerStatusRequestSent = false }
                    .flatMap {
                        Observable.just(it.isSuccessful)
                    }
        } else {
            Observable.empty()
        }
    }

    fun getPatriotIndex(): Observable<Subject> {
        return if (!isPiRequestSent) {
            isPiRequestSent = true
            contentRepository.getSubjectInfo(token, LoggedSubject.birthId)
                    .subscribeOn(Schedulers.io())
                    .doOnNext { isPiRequestSent = false }
                    .doOnError { isPiRequestSent = false }
        } else {
            Observable.empty()
        }
    }

    fun getToken(): String {
        return token
    }
}