package xyz.siddharthseth.crostata.data.repository

import rx.Observable
import xyz.siddharthseth.crostata.data.Subject
import xyz.siddharthseth.crostata.data.Token
import xyz.siddharthseth.crostata.data.service.CrostataApiService

class LoginRepository(private val crostataApiService: CrostataApiService) {
    fun signIn(subject: Subject): Observable<Token> {
        return crostataApiService.signIn(subject.birthId, subject.password)
    }
}