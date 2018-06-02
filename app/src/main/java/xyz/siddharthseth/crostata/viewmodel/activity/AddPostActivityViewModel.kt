package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.net.Uri
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
                if (!isUploadRequestSent) {
                    isUploadRequestSent = true
                    return contentRepository.submitImagePost(token, LoggedSubject.birthId, text, imageFile, false)
                            .subscribeOn(Schedulers.io())
                            .doOnNext { isUploadRequestSent = false }
                            .doOnError { isUploadRequestSent = false }
                            .flatMap {
                                if (it.isSuccessful)
                                    return@flatMap Observable.just(true)
                                else
                                    return@flatMap Observable.just(false)
                            }

                }
            }
        } else {
            if (!isUploadRequestSent) {
                isUploadRequestSent = true
                return contentRepository.submitTextPost(token, LoggedSubject.birthId, text, false)
                        .subscribeOn(Schedulers.io())
                        .doOnNext { isUploadRequestSent = false }
                        .doOnError { isUploadRequestSent = false }
                        .flatMap {
                            if (it.isSuccessful)
                                return@flatMap Observable.just(true)
                            else
                                return@flatMap Observable.just(false)
                        }
            }
        }
        return Observable.empty()
    }


    var isImagePost: Boolean = false
    private var isUploadRequestSent = false
    lateinit var imageFile: File
    val TAG: String = javaClass.simpleName
    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val sharedPreferences = SharedPreferencesService()
    internal val token: String = sharedPreferences.getToken(application)
    var imageUri: Uri? = null

}