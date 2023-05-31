package com.example.storiessocial.model.local.prefrence

data class UserModel(
    val email: String,
    val password: String,
    val token : String,
    val isLogin: Boolean
)