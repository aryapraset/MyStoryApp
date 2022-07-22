package com.bangkit.mystoryapp.utility

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUserToken(): Flow<String?>{
        return dataStore.data.map {
            it[TOKEN_KEY]
        }
    }
    fun getUserId(): Flow<String?> {
        return dataStore.data.map {
            it[ID_KEY]
        }
    }
    fun getUserName(): Flow<String?>{
        return dataStore.data.map{
            it[NAME_KEY]
        }
    }
    fun getUserEmail(): Flow<String?>{
        return dataStore.data.map {
            it[EMAIL_KEY]
        }
    }
    suspend fun saveUserToken(token: String){
        dataStore.edit {
            it[TOKEN_KEY] = token
        }
    }

    suspend fun saveUserId(id: String) {
        dataStore.edit {
            it[ID_KEY] = id
        }
    }

    suspend fun saveUserEmail(email: String){
        dataStore.edit {
            it[EMAIL_KEY] = email
        }
    }
    suspend fun saveUserName(name: String){
        dataStore.edit {
            it[NAME_KEY] = name
        }
    }

    suspend fun clearUserData() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val NAME_KEY = stringPreferencesKey("name")
        private val ID_KEY = stringPreferencesKey("id")
        private val EMAIL_KEY = stringPreferencesKey("email")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}