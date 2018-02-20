package xyz.siddharthseth.crostata.data.service

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.Display
import xyz.siddharthseth.crostata.data.model.Subject
import xyz.siddharthseth.crostata.data.model.Token

class SharedPrefrencesService {
    fun saveToken(token: Token, context: Context): Boolean {
        val sharedPrefrences: SharedPreferences = context.getSharedPreferences("subject", 0)
        sharedPrefrences.edit().putString("token", token.tokenValue).apply()

        return true
    }

    fun getToken(context: Context): String {
        val sharedPrefrences: SharedPreferences = context.getSharedPreferences("subject", 0)
        return sharedPrefrences.getString("token", "")

    }

    fun saveSubjectDetails(subject: Subject, context: Context): Boolean {
        val sharedPrefrences: SharedPreferences = context.getSharedPreferences("subject", 0)
        sharedPrefrences.edit()
                .putString("birthId", subject.birthId)
                .putString("password", subject.password)
                .apply()

        return true
    }

    fun getUserDetails(context: Context): Subject {
        val sharedPrefrences: SharedPreferences = context.getSharedPreferences("subject", 0)
        return Subject.getInstance(sharedPrefrences.getString("birthId", "")
                , sharedPrefrences.getString("password", ""))
    }
}