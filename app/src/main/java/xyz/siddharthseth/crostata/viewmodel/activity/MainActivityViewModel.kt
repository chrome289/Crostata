package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

/**
 * viewmodel for main activity
 */
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * get title for toolbar
     * @param fragmentId id for selected fragment
     */
    internal fun getToolbarTitle(fragmentId: Int): String {
        val context: Context = getApplication()
        return when (fragmentId) {
            R.id.community -> context.resources.getString(R.string.toolbar_community)
            R.id.vigilance -> context.resources.getString(R.string.toolbar_vigilance)
            R.id.viewPost -> context.resources.getString(R.string.comments)
            R.id.about -> context.resources.getString(R.string.about_crostata)
            else -> context.resources.getString(R.string.toolbar_home)
        }
    }

    /**
     * check if network is available
     */
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

    /**
     * get token value
     */
    fun getToken(): String {
        return token
    }

    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val token = SharedPreferencesService().getToken(application)
    //static stuff
    companion object {
        private val TAG: String = this::class.java.simpleName
        var isDetailActivityOpen: Boolean = false
        private var isServerStatusRequestSent = false
    }
}