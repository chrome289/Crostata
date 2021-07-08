package xyz.siddharthseth.crostata.data.dao.local.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.repository.preference.SharedPreferenceRepository

class SharedPreferencesDao(context: Context) : SharedPreferenceRepository {
    override fun getEmail(): String {
        return sharedPreferences.getString(KEY_SHARED_PREFERENCE_EMAIL, "").toString()
    }

    override fun putEmail(email: String) {
        sharedPreferences.edit().putString(KEY_SHARED_PREFERENCE_EMAIL, email).apply()
    }

    override fun getPassword(): String {
        return sharedPreferences.getString(KEY_SHARED_PREFERENCE_PASSWORD, "").toString()
    }

    override fun putPassword(password: String) {
        sharedPreferences.edit().putString(KEY_SHARED_PREFERENCE_PASSWORD, password).apply()
    }

    override fun getToken(): String {
        return sharedPreferences.getString(KEY_SHARED_PREFERENCE_TOKEN, "").toString()
    }

    override fun putToken(token: String) {
        sharedPreferences.edit().putString(KEY_SHARED_PREFERENCE_TOKEN, token).apply()
    }

    override fun cleanup() {
        sharedPreferences.edit().clear().apply()
    }

    private var sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private val KEY_SHARED_PREFERENCE_APP_THEME = context.getString(R.string.key_app_theme)

    private val KEY_SHARED_PREFERENCE_TOKEN = "token"
    private val KEY_SHARED_PREFERENCE_EMAIL = "email"
    private val KEY_SHARED_PREFERENCE_PASSWORD = "password"
}
