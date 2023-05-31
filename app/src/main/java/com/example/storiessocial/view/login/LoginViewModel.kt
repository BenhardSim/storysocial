package com.example.storiessocial.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storiessocial.model.AppRespository
import com.example.storiessocial.model.local.prefrence.UserModel
import com.example.storiessocial.model.remote.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LoginViewModel(private val appRespository: AppRespository) : ViewModel() {

    suspend fun login(email: String, password: String): Flow<Result<LoginResponse>> = appRespository.login(email,password)

    fun saveUser(user: UserModel){
        viewModelScope.launch {
            appRespository.saveUser(user)
        }
    }

}