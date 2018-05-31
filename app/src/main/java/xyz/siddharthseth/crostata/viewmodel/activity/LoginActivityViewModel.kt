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


class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "LoginActivityViewModel"
    private val sharedPreferencesService = SharedPreferencesService()
    var isSignInRequestSent = false


    fun signIn(birthId: String, password: String): Observable<Int> {
        LoggedSubject.init(birthId, password)
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
                            } else if (!token.success && responseToken.code() == 404)
                                Observable.just(1)
                            else
                                Observable.just(2)
                        }
                    })
        } else {
            Observable.empty()
        }
    }
}