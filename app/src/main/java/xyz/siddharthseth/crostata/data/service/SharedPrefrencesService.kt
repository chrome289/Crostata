package xyz.siddharthseth.crostata.data.service

import android.content.Context
import android.content.SharedPreferences
import xyz.siddharthseth.crostata.data.model.Subject
import xyz.siddharthseth.crostata.data.model.retrofit.Token

class SharedPrefrencesService {
    fun saveToken(token: Token, context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("subject", 0)
        sharedPreferences.edit().putString("token", token.tokenValue).apply()

        return true
    }

    fun getToken(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("subject", 0)
        return sharedPreferences.getString("token", "")

    }

    fun saveSubjectDetails(subject: Subject, context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("subject", 0)
        sharedPreferences.edit()
                .putString("birthId", subject.birthId)
                .putString("password", subject.password)
                .apply()

        return true
    }

    fun getUserDetails(context: Context): Subject {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("subject", 0)
        return Subject.getInstance(sharedPreferences.getString("birthId", "")
                , sharedPreferences.getString("password", ""))
    }
}