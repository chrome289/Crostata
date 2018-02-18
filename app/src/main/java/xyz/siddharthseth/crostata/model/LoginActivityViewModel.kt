package xyz.siddharthseth.crostata.model


import android.util.Log
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.Subject
import xyz.siddharthseth.crostata.data.Token
import xyz.siddharthseth.crostata.data.providers.LoginRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.LoginRepository

class LoginActivityViewModel {

    private val TAG = "LoginActivityViewModel"
    var signInSuccessful: Boolean = false

    fun signIn(birthId: String, password: String): Observable<Boolean> {

        val subject = Subject.getInstance(birthId, password)

        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()

        loginRepository.signIn(subject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    processLoginResult(result)
                    Log.v(TAG, result.tokenValue)
                    if (result.tokenValue.isNotBlank())
                        signInSuccessful = true
                }, { error -> error.printStackTrace() })

        return Observable.just(signInSuccessful)
    }

    private fun processLoginResult(result: Token) {
        //if(result.tokenValue.length==0)

    }
}