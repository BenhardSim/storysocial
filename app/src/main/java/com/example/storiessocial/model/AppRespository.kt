package com.example.storiessocial.model

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.*
import com.example.storiessocial.model.local.prefrence.UserModel
import com.example.storiessocial.model.local.prefrence.UserPreference
import com.example.storiessocial.model.local.entity.StoryItem
import com.example.storiessocial.model.local.room.StoryDatabase
import com.example.storiessocial.model.mediator.StoryRemoteMediator
import com.example.storiessocial.model.remote.response.*
import com.example.storiessocial.model.remote.retrofit.APIConfig
import com.example.storiessocial.model.remote.retrofit.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlin.Result

class AppRespository(
    private val pref : UserPreference,
    private val database: StoryDatabase,
    private val apiService: APIService
) {

    suspend fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try{
            val response = APIConfig.getApiService().userLogin(email, password)
            emit(Result.success(response))
        }catch(e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun register(name: String, email: String, password: String): Flow<Result<RegisterResponse>> = flow {
        try{
            val response = APIConfig.getApiService().userRegister(name,email,password)
            emit(Result.success(response))
        }catch(e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveUser(user: UserModel) {
        pref.saveUser(user)
    }

    suspend fun postStoryResponse(token: String, file: MultipartBody.Part, desc: RequestBody, lon: Double?, lat: Double?): AddStoryResponse{
        return APIConfig.getApiService().addStories(token,file,desc,lat,lon)
    }

    fun getAllStoriesWithPage(token: String): LiveData<PagingData<StoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(database, apiService, token),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getAllStoriesWithLocation(token: String): StoriesResponse{
        Log.e("get loc", APIConfig.getApiService().allStoriesWithLocation(token, size = 20).toString())
        return APIConfig.getApiService().allStoriesWithLocation(token, size = 20)
    }

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    companion object {
        @Volatile
        private var instance: AppRespository? = null
        fun getInstance(
            pref : UserPreference,
            database: StoryDatabase,
            apiService: APIService
        ): AppRespository =
            instance ?: synchronized(this) {
                instance ?: AppRespository(pref,database,apiService)
            }.also { instance = it }
    }

}