package xyz.siddharthseth.crostata.data.repository

import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.Token
import xyz.siddharthseth.crostata.data.service.CrostataApiService

class LoginRepository(private val crostataApiService: CrostataApiService) {

    /**
     * sign in loudly
     * @param birthId BirthId , length = 10
     * @param password password, length = 32ish
     */
    fun signIn(birthId: String, password: String): Observable<Response<Token>> {
        return crostataApiService.signIn(birthId, password)
    }

    /**
     * sign in silently
     * @param token saved token
     */
    fun signInSilently(token: String): Observable<Response<ResponseBody>> {
        return crostataApiService.signInSilently(token)
    }

}