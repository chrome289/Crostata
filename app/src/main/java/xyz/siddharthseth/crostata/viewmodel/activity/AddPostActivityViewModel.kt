package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import java.io.File

class AddPostActivityViewModel(application: Application) : AndroidViewModel(application) {

    fun submitPost(text: String): Observable<Boolean> {
        if (isImagePost) {
            if (!imageFile.exists()) {
                Log.v(TAG, "file not found $imageFile")
                return Observable.just(false)
            } else {
                return contentRepository.submitImagePost(token, LoggedSubject.birthId, text, imageFile, false)
                        .subscribeOn(Schedulers.io())
                        .flatMap {
                            if (it.isSuccessful)
                                return@flatMap Observable.just(true)
                            else
                                return@flatMap Observable.just(false)
                        }
            }
        } else {
            return contentRepository.submitTextPost(token, LoggedSubject.birthId, text, false)
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        if (it.isSuccessful)
                            return@flatMap Observable.just(true)
                        else
                            return@flatMap Observable.just(false)
                    }
        }
    }

    var isImagePost: Boolean = false
    lateinit var imageFile: File
    val TAG: String = javaClass.simpleName
    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val sharedPreferences = SharedPreferencesService()
    private val token: String = sharedPreferences.getToken(application)

}