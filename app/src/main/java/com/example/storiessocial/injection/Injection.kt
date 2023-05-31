package com.example.storiessocial.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storiessocial.model.AppRespository
import com.example.storiessocial.model.local.prefrence.UserPreference
import com.example.storiessocial.model.local.room.StoryDatabase
import com.example.storiessocial.model.remote.retrofit.APIConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
    fun provideRepository(context: Context): AppRespository {
        val pref = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        val apiService = APIConfig.getApiService()
        return AppRespository.getInstance(pref,database,apiService)
    }

    fun providePreferences(context: Context) : UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }
}