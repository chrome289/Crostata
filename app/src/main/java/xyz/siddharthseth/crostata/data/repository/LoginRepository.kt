package xyz.siddharthseth.crostata.data.repository

import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.retrofit.Token
import xyz.siddharthseth.crostata.data.service.CrostataApiService

class LoginRepository(private val crostataApiService: CrostataApiService) {

    val TAG = "LoginRepository"

    fun signIn(subject: LoggedSubject): Observable<Response<Token>> {
        return crostataApiService.signIn(subject.birthId, subject.password)
    }

    fun signInSilently(token: String): Observable<Response<ResponseBody>> {
        return crostataApiService.signInSilently(token)
    }
}