package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.net.Uri
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import java.io.File

/**
 * viewmodel for add post activity
 */
class AddPostActivityViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * helper for submitting posts
     * @param contentText text content for the post
     */
    fun submitPost(contentText: String): Observable<Boolean> {
        if (!isUploadRequestSent) {
            return if (isImagePost) {
                if (!imageFile.exists()) {
                    Observable.just(false)
                } else {
                    isUploadRequestSent = true
                    submitHybridPost(contentText)
                }

            } else {
                isUploadRequestSent = true
                submitTextPost(contentText)
            }
        } else {
            return Observable.empty()
        }
    }

    /**
     * submit text post
     * @param contentText text content for the post
     */
    private fun submitTextPost(contentText: String): Observable<Boolean> {
        return contentRepository.submitTextPost(token, LoggedSubject.birthId, contentText, false)
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

    /**
     * submit hybrid post
     * @param contentText text content for the post
     */
    private fun submitHybridPost(contentText: String): Observable<Boolean> {
        return contentRepository.submitImagePost(token, LoggedSubject.birthId, contentText, imageFile, false)
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

    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private val sharedPreferences = SharedPreferencesService()
    internal val token: String = sharedPreferences.getToken(application)

    //static stuff
    companion object {
        private val TAG: String = this::class.java.simpleName
        var isImagePost: Boolean = false
        private var isUploadRequestSent = false
        lateinit var imageFile: File
        var imageUri: Uri? = null
    }
}