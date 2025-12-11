package com.example.level_up_app.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.level_up_app.data.User

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_CLAVE = "user_clave"
        private const val KEY_IS_ADMIN = "is_admin"
        private const val KEY_CREATED_AT = "created_at"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(user: User) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_CLAVE, user.clave)
            putBoolean(KEY_IS_ADMIN, user.isAdmin)
            putString(KEY_CREATED_AT, user.createdAt)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): User? {
        if (!isLoggedIn()) return null

        return User(
            id = prefs.getLong(KEY_USER_ID, 0),
            name = prefs.getString(KEY_USER_NAME, "") ?: "",
            email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            clave = prefs.getString(KEY_USER_CLAVE, "") ?: "",
            isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false),
            createdAt = prefs.getString(KEY_CREATED_AT, "") ?: ""
        )
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

