package com.example.storiessocial.model.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storiessocial.model.local.entity.RemoteKeys
import com.example.storiessocial.model.local.entity.StoryItem
import com.example.storiessocial.model.local.room.StoryDatabase
import com.example.storiessocial.model.remote.retrofit.APIService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: APIService,
    private val token: String
    ) : RemoteMediator<Int, StoryItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try{
            val token = "Bearer $token"
            val responseData = apiService.allStories(
                token = token,
                page = page,
                size = state.config.pageSize)

            val listStory = responseData.listStory

            val endOfPaginationReached = listStory?.isEmpty()

            if(listStory!!.isEmpty() && loadType == LoadType.REFRESH){
                MediatorResult.Error(Exception("No Data Found"))
            } else{
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAll()
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached == true) null else page + 1
                    val keys: MutableList<RemoteKeys> = mutableListOf()
                    val listStoryEntity: MutableList<StoryItem> = mutableListOf()
                    listStory.map {
                        keys += RemoteKeys(it?.id!!, prevKey, nextKey)
                        listStoryEntity += StoryItem(
                            id = it?.id!!,
                            photoUrl = it.photoUrl,
                            createdAt = it.createdAt,
                            name = it.name,
                            description = it.description,
                            lon = it.lon,
                            lat = it.lat
                        )
                    }
                    database.remoteKeysDao().insertAll(keys)
                    database.storyDao().insertStory(listStoryEntity)
                }
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached == true)
            }
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }


    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            data.id.let { database.remoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            data.id.let { database.remoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}