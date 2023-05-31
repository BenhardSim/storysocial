package com.example.storiessocial.view.signup

import androidx.lifecycle.ViewModel
import com.example.storiessocial.model.AppRespository
import com.example.storiessocial.model.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

class SignupViewModel(private val AppRepo: AppRespository) : ViewModel() {
    suspend fun register(name: String, email: String, password: String): Flow<Result<RegisterResponse>> = AppRepo.register(name,email,password)
}