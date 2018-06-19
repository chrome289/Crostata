package xyz.siddharthseth.crostata.viewmodel.activity


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import rx.Observable
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.providers.LoginRepositoryProvider
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.repository.LoginRepository
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

/**
 * viewmodel for login activity
 */
class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * sign in
     * @param birthId birthId
     * @param password password
     */
    fun signIn(birthId: String, password: String): Observable<Int> {
        return if (!isSignInRequestSent) {
            isSignInRequestSent = true
            signInLoudly(birthId, password)
        } else {
            Observable.empty()
        }
    }

    //sign in loudly
    private fun signInLoudly(birthId: String, password: String): Observable<Int> {
        return loginRepository.signIn(birthId, password)
                .subscribeOn(Schedulers.io())
                .doOnNext { isSignInRequestSent = false }
                .doOnError { isSignInRequestSent = false }
                .flatMap { responseToken ->
                    val token = responseToken.body()
                    when (responseToken.code()) {
                    //everything checks out, save token
                        200 -> {
                            val isTokenSaved = sharedPreferencesService.saveToken(token!!, getApplication())
                            if (isTokenSaved) {
                                Observable.just(0)
                            } else {
                                Observable.just(3)
                            }
                        }
                    //no user found
                        404 -> {
                            Observable.just(1)
                        }
                    //password incorrect
                        403 -> {
                            Observable.just(2)
                        }
                    //random error
                        else -> {
                            Observable.just(3)
                        }
                    }
                }
    }

    /**
     * get user details
     * @param birthId birthId of user
     */
    fun getSubjectDetails(birthId: String): Observable<Subject> {
        return if (!isPiRequestSent) {
            isPiRequestSent = true
            token = sharedPreferencesService.getToken(getApplication())
            contentRepository.getSubjectInfo(token, birthId)
                    .subscribeOn(Schedulers.io())
                    .doOnNext {
                        isPiRequestSent = false
                    }
                    .doOnError { isPiRequestSent = false }
        } else {
            Observable.empty()
        }
    }

    /**
     * save user details in shared preferences
     * @param birthId birthId of user
     * @param password password for future loud logins
     * @param name name of user
     */
    fun saveSubjectDetails(birthId: String, password: String, name: String) {
        LoggedSubject.init(birthId, password, name)

        sharedPreferencesService.saveSubjectDetails(getApplication())
    }

    private val contentRepository: ContentRepository = ContentRepositoryProvider.getContentRepository()
    private var token = SharedPreferencesService().getToken(application)
    private val sharedPreferencesService = SharedPreferencesService()

    //static stuff
    companion object {
        val loginRepository: LoginRepository = LoginRepositoryProvider.getLoginRepository()
        private val TAG = this::class.java.simpleName
        var subject: Subject = Subject(LoggedSubject.birthId, "")
        private var isSignInRequestSent = false
        private var isPiRequestSent = false
    }
}