package xyz.siddharthseth.crostata.data.model

object Subject {
    var birthId = ""
    var password = ""

    fun getInstance(birthId: String, password: String): Subject {
        Subject.birthId = birthId
        Subject.password = password
        return this
    }

}