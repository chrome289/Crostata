package xyz.siddharthseth.crostata.viewmodel


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.Subject
import xyz.siddharthseth.crostata.data.providers.LoginRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.LoginRepository
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService


class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "LoginActivityViewModel"
    private val sharedPrefrencesService = SharedPrefrencesService()


    fun signIn(birthId: String, password: String): Observable<Int> {

        val subject = Subject.getInstance(birthId, password)

        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()


        return loginRepository.signIn(subject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap({ responseToken ->
                    val token = responseToken.body()
                    Log.v(TAG, "here")
                    if (token == null)
                        Observable.just(4)
                    else {
                        if (token.success) {
                            val isSavedLocally = sharedPrefrencesService.saveToken(token, getApplication())
                                    && sharedPrefrencesService.saveSubjectDetails(subject, getApplication())
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

    }

    fun signInSilently(token: String): Observable<Int> {
        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()

        return loginRepository.signInSilently(token)
                .subscribeOn(Schedulers.io())
                .flatMap({ response ->
                    Log.v(TAG, "here")
                    if (response.isSuccessful)
                        Observable.just(0)
                    else {
                        val subject = sharedPrefrencesService.getUserDetails(getApplication())
                        signIn(subject.birthId, subject.password)
                    }
                })

    }
}