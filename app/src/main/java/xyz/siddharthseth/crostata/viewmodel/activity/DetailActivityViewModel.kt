package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

class DetailActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val token = SharedPreferencesService().getToken(application)
    private val TAG: String = this::class.java.simpleName
    var isDetailActivityOpen: Boolean = false
    val toolbarTitle = ArrayList<String>()

    fun checkNetworkAvailable(): Observable<Boolean> {
        return contentRepository.serverStatus(token)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    Observable.just(it.isSuccessful)
                }
    }
}