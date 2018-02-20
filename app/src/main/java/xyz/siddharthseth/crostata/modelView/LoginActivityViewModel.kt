package xyz.siddharthseth.crostata.modelView


import android.content.Context
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.CheckToken
import xyz.siddharthseth.crostata.data.model.Subject
import xyz.siddharthseth.crostata.data.model.Token
import xyz.siddharthseth.crostata.data.providers.LoginRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.LoginRepository
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService


class LoginActivityViewModel {

    private val TAG = "LoginActivityViewModel"
    private val sharedPrefrencesService = SharedPrefrencesService()

    fun signIn(birthId: String, password: String, context: Context): Observable<Int> {

        val subject = Subject.getInstance(birthId, password)

        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()

        return loginRepository.signIn(subject, context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap({ token: Token? ->
                    Log.v(TAG, "here")
                    if (token == null)
                        Observable.just(4)
                    else {
                        if (token.resultCode == 0) {
                            val isSavedLocally = sharedPrefrencesService.saveToken(token, context)
                                    && sharedPrefrencesService.saveSubjectDetails(subject, context)
                            if (isSavedLocally)
                                Observable.just(0)
                            else
                                Observable.just(3)
                        } else if (token.resultCode == 1)
                            Observable.just(1)
                        else
                            Observable.just(2)
                    }
                })

    }

    fun signInSilently(token: String, context: Context): Observable<Int> {
        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()

        return loginRepository.signInSilently(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap({ checkToken: CheckToken ->
                    Log.v(TAG, "here")
                    if (checkToken.success)
                        Observable.just(0)
                    else {
                        val subject = sharedPrefrencesService.getUserDetails(context)
                        signIn(subject.birthId, subject.password, context)
                    }
                })

    }
}