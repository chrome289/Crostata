package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.providers.LoginRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.LoginRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

/**
 * viewmodel for splash activity
 */
class SplashActivityViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * sign in silently
     */
    fun performSilentSignIn(): Observable<Boolean> {
        return if (!isSignInSilentlyRequestSent) {
            isSignInSilentlyRequestSent = true
            signInSilently()
        } else {
            Observable.empty()
        }
    }

    /**
     * sign in loudly
     */
    fun performLoudSignIn(): Observable<Int> {
        return if (!isSignInRequestSent) {
            isSignInRequestSent = true
            signInLoudly()
        } else {
            Observable.empty()
        }
    }

    //sign in silently
    private fun signInSilently(): Observable<Boolean> {
        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()
        return loginRepository.signInSilently(token)
                .subscribeOn(Schedulers.io())
                .doOnNext { isSignInSilentlyRequestSent = false }
                .doOnError { isSignInSilentlyRequestSent = false }
                .flatMap { response ->
                    if (response.isSuccessful) {
                        Observable.just(true)
                    } else {
                        Observable.just(false)
                    }
                }
    }

    //sign in loudly
    private fun signInLoudly(): Observable<Int> {
        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()
        return loginRepository.signIn(LoggedSubject.birthId, LoggedSubject.password)
                .subscribeOn(Schedulers.io())
                .doOnNext { isSignInRequestSent = false }
                .doOnError { isSignInRequestSent = false }
                .flatMap { responseToken ->
                    val token = responseToken.body()
                    if (token == null)
                        Observable.just(4)
                    else {
                        if (token.success) {
                            val isSavedLocally = sharedPreferencesService.saveToken(token, getApplication())
                            if (isSavedLocally)
                                Observable.just(0)
                            else
                                Observable.just(3)
                        } else if (!token.success && responseToken.code() == 404) {
                            Observable.just(1)
                        } else {
                            Observable.just(2)
                        }
                    }
                }
    }

    /**
     * check if logged subject has been initialized
     */
    fun savedLoginDetailsAvailable(): Boolean {
        return LoggedSubject.isInitDone()
    }

    private val sharedPreferencesService = SharedPreferencesService()
    val token = sharedPreferencesService.getToken(application)

    companion object {
        private val TAG: String = this::class.java.simpleName
        private var isSignInSilentlyRequestSent = false
        private var isSignInRequestSent = false
    }
}