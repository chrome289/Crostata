package xyz.siddharthseth.crostata.data.model

import android.content.Context
import android.content.SharedPreferences
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
        sharedPreferencesService.saveSubjectDetails(context)
    }

    fun init(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("subject", 0)
        init(sharedPreferences.getString("birthId", "")
                , sharedPreferences.getString("password", "")
                , sharedPreferences.getString("name", ""))
    }

    fun isInitDone(): Boolean {
        return birthId.length == 9
    }
}