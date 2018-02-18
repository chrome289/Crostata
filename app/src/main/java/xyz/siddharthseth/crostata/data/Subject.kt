package xyz.siddharthseth.crostata.data

object Subject {
    var birthId = ""
    var password = ""

    fun getInstance(birthId: String, password: String): Subject {
        this.birthId = birthId
        this.password = password
        return this
    }
}