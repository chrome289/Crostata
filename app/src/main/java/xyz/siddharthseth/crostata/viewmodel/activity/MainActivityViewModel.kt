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

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val token = SharedPreferencesService().getToken(application)
    private val TAG: String = this::class.java.simpleName

    internal fun getToolbarTitle(fragmentId: Int): String {
        val context: Context = getApplication()
        when (fragmentId) {
            R.id.home -> return context.resources.getString(R.string.toolbar_home)
            R.id.community -> return context.resources.getString(R.string.toolbar_community)
            R.id.selfProfile -> return context.resources.getString(R.string.toolbar_profile)
            R.id.vigilance -> return context.resources.getString(R.string.toolbar_vigilance)
        }
        return ""
    }

    fun checkNetworkAvailable(): Observable<Boolean> {
        return contentRepository.serverStatus(token)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    Observable.just(it.isSuccessful)
                }
    }
}