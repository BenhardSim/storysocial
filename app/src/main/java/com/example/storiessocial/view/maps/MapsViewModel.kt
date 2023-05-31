package com.example.storiessocial.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storiessocial.model.AppRespository
import com.example.storiessocial.model.Result
import com.example.storiessocial.model.local.prefrence.UserModel
import com.example.storiessocial.model.remote.response.StoriesResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val appRepo: AppRespository) : ViewModel() {
    fun getToken(): LiveData<UserModel> {
        return appRepo.getToken()
    }

    private val allStoriesLiveData = MutableLiveData<Result<StoriesResponse>>()

    fun allStories(token: String): LiveData<Result<StoriesResponse>> {
        viewModelScope.launch {
            allStoriesLiveData.value = Result.Loading
            try{
                val bearerToken = "Bearer $token"
                val response = appRepo.getAllStoriesWithLocation(bearerToken)
                Log.e("loc view model", response.toString())
                allStoriesLiveData.value = Result.Success(response)
            }catch(e: Exception) {
                Log.e("viewmodel",e.toString())
            }
        }
        return allStoriesLiveData
    }

}