package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.providers.LoginRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.LoginRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

class SplashActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = javaClass.simpleName
    private val sharedPreferencesService = SharedPreferencesService()
    val token = sharedPreferencesService.getToken(application)
    private var isSignInSilentlyRequestSent = false
    private var isSignInRequestSent = false

    init {
        initLoggedSubject()
    }

    fun signInSilently(): Observable<Boolean> {
        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()
        return if (!isSignInSilentlyRequestSent) {
            isSignInSilentlyRequestSent = true
            loginRepository.signInSilently(token)
                    .subscribeOn(Schedulers.io())
                    .doOnNext { isSignInSilentlyRequestSent = false }
                    .doOnError { isSignInSilentlyRequestSent = false }
                    .flatMap({ response ->
                        Log.v(TAG, "here")
                        if (response.isSuccessful) {
                            Observable.just(true)
                        } else {
                            Observable.just(false)
                        }
                    })
        } else {
            Observable.empty()
        }
    }

    fun signIn(): Observable<Int> {
        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()

        return if (!isSignInRequestSent) {
            isSignInRequestSent = true
            loginRepository.signIn()
                    .subscribeOn(Schedulers.io())
                    .doOnNext { isSignInRequestSent = false }
                    .doOnError { isSignInRequestSent = false }
                    .flatMap({ responseToken ->
                        val token = responseToken.body()
                        Log.v(TAG, "here")
                        if (token == null)
                            Observable.just(4)
                        else {
                            if (token.success) {
                                val isSavedLocally = sharedPreferencesService.saveToken(token, getApplication())
                                        && sharedPreferencesService.saveSubjectDetails(getApplication())
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
                    })
        } else {
            Observable.empty()
        }
    }

    fun savedLoginDetailsAvailable(): Boolean {
        return LoggedSubject.isInitDone()
    }

    fun initLoggedSubject() {
        LoggedSubject.init(getApplication())
    }
}