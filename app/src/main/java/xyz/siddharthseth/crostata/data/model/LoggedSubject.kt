package xyz.siddharthseth.crostata.data.model

object LoggedSubject {
    var birthId = ""
    var password = ""

    fun getInstance(birthId: String, password: String): LoggedSubject {
        LoggedSubject.birthId = birthId
        LoggedSubject.password = password
        return this
    }

}