package com.example.storiessocial

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storiessocial.injection.Injection
import com.example.storiessocial.model.AppRespository
import com.example.storiessocial.model.local.prefrence.UserPreference
import com.example.storiessocial.view.addStory.AddStoryViewModel
import com.example.storiessocial.view.login.LoginViewModel
import com.example.storiessocial.view.main.MainViewModel
import com.example.storiessocial.view.maps.MapsViewModel
import com.example.storiessocial.view.signup.SignupViewModel

class ViewModelFactory(private val pref: UserPreference, private val appRepo : AppRespository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref,appRepo) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(appRepo) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(appRepo) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(appRepo) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(appRepo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.providePreferences((context)),Injection.provideRepository(context))
            }.also { instance = it }
    }


}