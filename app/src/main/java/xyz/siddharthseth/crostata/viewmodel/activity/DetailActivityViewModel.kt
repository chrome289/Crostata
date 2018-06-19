package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

/**
 * viewmodel for detail activity
 */
class DetailActivityViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * check if network is available
     */
    fun checkNetworkAvailable(): Observable<Boolean> {
        return if (!isRequestSent) {
            isRequestSent = true
            contentRepository.serverStatus(token)
                    .subscribeOn(Schedulers.io())
                    .doOnNext { isRequestSent = false }
                    .doOnError { isRequestSent = false }
                    .flatMap {
                        Observable.just(it.isSuccessful)
                    }
        } else {
            Observable.empty()
        }
    }

    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val token = SharedPreferencesService().getToken(application)

    //static stuff
    companion object {
        private val TAG: String = this::class.java.simpleName
        var isDetailActivityOpen: Boolean = false
        val toolbarTitle = ArrayList<String>()
        private var isRequestSent = false
    }
}