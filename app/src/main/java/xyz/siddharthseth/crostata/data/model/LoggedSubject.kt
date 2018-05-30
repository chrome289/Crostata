package xyz.siddharthseth.crostata.data.model

import android.content.Context
import android.content.SharedPreferences
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

object LoggedSubject {
    var birthId = ""
    var password = ""

    fun init(birthId: String, password: String): LoggedSubject {
        LoggedSubject.birthId = birthId
        LoggedSubject.password = password
        return this
    }

    fun clear(context: Context) {
        val sharedPreferencesService = SharedPreferencesService()
        init("", "")
        sharedPreferencesService.saveSubjectDetails(context)
    }

    fun init(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("subject", 0)
        init(sharedPreferences.getString("birthId", "")
                , sharedPreferences.getString("password", ""))
    }

    fun isInitDone(): Boolean {
        return birthId.length == 9
    }
}