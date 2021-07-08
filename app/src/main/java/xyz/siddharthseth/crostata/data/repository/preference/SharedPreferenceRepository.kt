package xyz.siddharthseth.crostata.data.repository.preference


interface SharedPreferenceRepository {
    fun getEmail(): String
    fun putEmail(email: String)

    fun getPassword(): String
    fun putPassword(password: String)

    fun getToken(): String
    fun putToken(token: String)

    fun cleanup()
}