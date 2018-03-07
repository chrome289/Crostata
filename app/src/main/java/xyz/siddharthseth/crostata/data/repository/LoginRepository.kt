package xyz.siddharthseth.crostata.data.repository

import retrofit2.Response
import rx.Observable
import xyz.siddharthseth.crostata.data.model.Subject
import xyz.siddharthseth.crostata.data.model.retrofit.CheckToken
import xyz.siddharthseth.crostata.data.model.retrofit.Token
import xyz.siddharthseth.crostata.data.service.CrostataApiService

class LoginRepository(private val crostataApiService: CrostataApiService) {

    val TAG = "LoginRepository"

    fun signIn(subject: Subject): Observable<Response<Token>> {
        return crostataApiService.signIn(subject.birthId, subject.password)
    }

    fun signInSilently(token: String): Observable<CheckToken> {
        return crostataApiService.signInSilently(token)
    }
}