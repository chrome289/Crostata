package xyz.siddharthseth.crostata.data.model

import android.content.Context
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

object LoggedSubject {
    var birthId = ""
    var password = ""
    var name = ""

    fun init(birthId: String, password: String, name: String): LoggedSubject {
        LoggedSubject.birthId = birthId
        LoggedSubject.password = password
        LoggedSubject.name = name

        return this
    }

    fun clear(context: Context) {
        val sharedPreferencesService = SharedPreferencesService()
        init("", "", "")
        SharedPreferencesService().deleteSubjectDetails(context)
    }

    fun init(context: Context) {
        SharedPreferencesService().initLoggedSubject(context)
    }

    fun isInitDone(): Boolean {
        return birthId.length == 9
    }
}