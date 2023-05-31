package com.example.storiessocial.view.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storiessocial.model.AppRespository
import com.example.storiessocial.model.local.prefrence.UserModel
import com.example.storiessocial.model.local.prefrence.UserPreference
import com.example.storiessocial.model.local.entity.StoryItem
import kotlinx.coroutines.launch


class MainViewModel(private val pref: UserPreference, private val appRepo: AppRespository) : ViewModel() {

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getToken(): LiveData<UserModel> {
        return appRepo.getToken()
    }

    fun getAllStoriesWithPaging(token: String): LiveData<PagingData<StoryItem>> = appRepo.getAllStoriesWithPage(token).cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}