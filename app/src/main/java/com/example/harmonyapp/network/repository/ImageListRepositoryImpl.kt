package com.example.harmonyapp.network.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.harmonyapp.data.db.ImageListDb
import com.example.harmonyapp.data.db.dao.ImageListDao
import com.example.harmonyapp.data.model.ImageListItem
import com.example.harmonyapp.data.paging.datasource.ImagePagingDataSource
import com.example.harmonyapp.data.paging.remotemediator.ImageListRemoteMediator
import com.example.harmonyapp.network.api.PicsumApi
import com.example.harmonyapp.utils.Constants.Companion.QUERY_PER_PAGE

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
class ImageListRepositoryImpl @Inject constructor(
    private val picsumApi: PicsumApi,
    private val localDataSource: ImageListDao,
    private val imageListDb: ImageListDb,
    val imageLimit: MutableLiveData<Int>
) : ImageListRepository {

    override suspend fun getImages(): Flow<PagingData<ImageListItem>> {
        val pagingSourceRemote = { ImagePagingDataSource(picsumApi) }
        val pagingSourceLocal = { imageListDb.getImageListDao().getAllImages() }
        return Pager(
            config = PagingConfig(pageSize = QUERY_PER_PAGE, prefetchDistance = 2),
            remoteMediator = ImageListRemoteMediator(picsumApi, imageListDb, imageLimit),
            pagingSourceFactory = pagingSourceLocal
        ).flow
    }

    override suspend fun saveImageItem(item: ImageListItem) = localDataSource.upsert(item)

    override suspend fun getSavedImages() = localDataSource.getAllImages()

    override suspend fun getSavedImagesList() = localDataSource.getAllImagesList()

    override
    suspend fun deleteAllImages() = localDataSource.deleteAllImages()
}