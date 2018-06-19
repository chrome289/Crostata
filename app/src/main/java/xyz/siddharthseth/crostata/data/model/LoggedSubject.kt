package xyz.siddharthseth.crostata.data.model

import android.content.Context
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService

/**
 *logged subject singleton
 */
object LoggedSubject {
    var birthId = ""
    var password = ""
    var name = ""

    /**
     *init with new info
    */
    fun init(birthId: String, password: String, name: String): LoggedSubject {
        LoggedSubject.birthId = birthId
        LoggedSubject.password = password
        LoggedSubject.name = name

        return this
    }

    /**
     * clear logged subject and saved info
     */
    fun clear(context: Context) {
        init("", "", "")
        SharedPreferencesService().deleteSubjectDetails(context)
    }

    /**
     * init from saved info
     */
    fun init(context: Context) {
        SharedPreferencesService().initLoggedSubject(context)
    }

    /**
     *is init done
     */
    fun isInitDone(): Boolean {
        return birthId.length == 9
    }
}