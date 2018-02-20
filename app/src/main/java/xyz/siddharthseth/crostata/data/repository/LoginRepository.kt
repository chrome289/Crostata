package xyz.siddharthseth.crostata.data.repository

import android.content.Context
import android.util.Log
import io.reactivex.internal.util.HalfSerializer.onNext
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.functions.Action1
import xyz.siddharthseth.crostata.data.model.CheckToken
import xyz.siddharthseth.crostata.data.model.Subject
import xyz.siddharthseth.crostata.data.model.Token
import xyz.siddharthseth.crostata.data.service.CrostataApiService
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService

class LoginRepository(private val crostataApiService: CrostataApiService) {

    val TAG = "LoginRepository"

    fun signIn(subject: Subject, context: Context): Observable<Token> {
        return crostataApiService.signIn(subject.birthId, subject.password)
    }

    fun signInSilently(token:String):Observable<CheckToken>{
        return crostataApiService.signInSilently(token)
    }
}