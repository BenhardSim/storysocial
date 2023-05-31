package com.example.storiessocial.view.addStory

import androidx.lifecycle.*
import com.example.storiessocial.model.AppRespository
import com.example.storiessocial.model.local.prefrence.UserModel
import com.example.storiessocial.model.remote.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val appRepo: AppRespository) : ViewModel() {

    fun getToken(): LiveData<UserModel> {
        return appRepo.getToken()
    }

    private val postRes = MutableLiveData<com.example.storiessocial.model.Result<AddStoryResponse>>()

    fun postStory(token: String, file: MultipartBody.Part, desc: RequestBody, lon: Double?,lat: Double?): LiveData<com.example.storiessocial.model.Result<AddStoryResponse>>{
        viewModelScope.launch {
            postRes.value = com.example.storiessocial.model.Result.Loading
            try{
                val bearerToken = "Bearer $token"
                val response = appRepo.postStoryResponse(bearerToken,file,desc,lon,lat)
                postRes.value = com.example.storiessocial.model.Result.Success(response)
            }catch(e: Exception) {
                postRes.value = com.example.storiessocial.model.Result.Error(e.toString())
            }
        }
        return postRes
    }

}