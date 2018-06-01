package xyz.siddharthseth.crostata.viewmodel.activity


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.providers.LoginRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.repository.LoginRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService


class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "LoginActivityViewModel"
    var subject: Subject = Subject(LoggedSubject.birthId, "")
    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private var token = SharedPreferencesService().getToken(application)
    private val sharedPreferencesService = SharedPreferencesService()
    var isSignInRequestSent = false
    var isPiRequestSent = false

    fun signIn(birthId: String, password: String): Observable<Int> {
        LoggedSubject.init(birthId, password, "")
        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()

        if (!isSignInRequestSent) {
            isSignInRequestSent = true
            return loginRepository.signIn()
                    .subscribeOn(Schedulers.io())
                    .doOnNext { isSignInRequestSent = false }
                    .doOnError { isSignInRequestSent = false }
                    .flatMap({ responseToken ->
                        val token = responseToken.body()
                        Log.v(TAG, "here")
                        when (responseToken.code()) {
                            200 -> {
                                val isTokenSaved = sharedPreferencesService.saveToken(token!!, getApplication())
                                if (isTokenSaved) {
                                    Observable.just(0)
                                } else {
                                    Observable.just(3)
                                }
                            }
                            404 -> {
                                Observable.just(1)
                            }
                            403 -> {
                                Observable.just(2)
                            }
                            else -> {
                                Observable.just(3)
                            }
                        }
                    })
        } else {
            return Observable.empty()
        }
    }


    fun getSubjectDetails(): Observable<Subject> {
        return if (!isPiRequestSent) {
            isPiRequestSent = true
            token = sharedPreferencesService.getToken(getApplication())
            contentRepository.getSubjectInfo(token, LoggedSubject.birthId)
                    .subscribeOn(Schedulers.io())
                    .doOnNext {
                        isPiRequestSent = false;
                    }
                    .doOnError { isPiRequestSent = false }
        } else {
            Observable.empty()
        }
    }

    fun saveSubjectDetails(name: String) {
        LoggedSubject.init(LoggedSubject.birthId, LoggedSubject.password, name)
    }
}