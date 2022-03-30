package com.example.harmonyapp.ui.main

import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.harmonyapp.base.BaseViewModel
import com.example.harmonyapp.data.model.ImageListItem
import com.example.harmonyapp.network.repository.ImageListRepositoryImpl

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
@ExperimentalPagingApi
class MainViewModel @Inject constructor(
    private val repository: ImageListRepositoryImpl
) : BaseViewModel() {

    private val TAG = "MainViewModel"
    private lateinit var _imageResponse: Flow<PagingData<ImageListItem>>
    val imageResponse: Flow<PagingData<ImageListItem>>
        get() = _imageResponse

    init {
        fetchImages()
    }

    private fun fetchImages() {
        launchPagingAsync({
            repository.getImages().cachedIn(viewModelScope)
        }, {
            _imageResponse = it
        })
    }

    fun onImageLimitChange(limit: Int){
        repository.imageLimit.value = if(limit==0) -1 else limit
    }

}