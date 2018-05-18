package xyz.siddharthseth.crostata.data.model

import android.content.Context
import android.content.SharedPreferences

object LoggedSubject {
    var birthId = ""
    var password = ""

    fun init(birthId: String, password: String): LoggedSubject {
        LoggedSubject.birthId = birthId
        LoggedSubject.password = password
        return this
    }

    fun init(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("subject", 0)
        init(sharedPreferences.getString("birthId", "")
                , sharedPreferences.getString("password", ""))
    }
}