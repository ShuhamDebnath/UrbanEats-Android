package com.shuham.urbaneats.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// 1. Singleton Instance (Prevents multiple file access errors)
private val Context.dataStore by preferencesDataStore("user_session_prefs")

// 2. Simple Data Class to hold session info
data class UserSession(
    val id: String?,
    val name: String?,
    val email: String?,
    val token: String?
)
class TokenManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }

    // 1. Save User Session
    suspend fun saveSession(token: String, id: String, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = id
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_EMAIL] = email
        }
    }

    // 2. Get Token (For API calls)
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[KEY_TOKEN] }
    }

    // 3. Get User Details (For Profile/Order)
    fun getUserSession(): Flow<UserSession> {
        return context.dataStore.data.map { prefs ->
            UserSession(
                id = prefs[KEY_USER_ID],
                name = prefs[KEY_USER_NAME],
                email = prefs[KEY_USER_EMAIL],
                token = prefs[KEY_TOKEN]
            )
        }
    }

    // 4. Logout (Clear Data)
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}

