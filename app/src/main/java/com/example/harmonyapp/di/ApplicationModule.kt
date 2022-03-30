package com.example.harmonyapp.di

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import com.example.harmonyapp.BuildConfig
import com.example.harmonyapp.data.db.ImageListDb
import com.example.harmonyapp.data.db.dao.ImageListDao
import com.example.harmonyapp.network.api.PicsumApi
import com.example.harmonyapp.network.repository.ImageListRepositoryImpl
import com.example.harmonyapp.utils.Constants.Companion.BASE_URL
import com.example.harmonyapp.utils.Constants.Companion.TAG
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@OptIn(ExperimentalPagingApi::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d(TAG, message)
            }
        })
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else OkHttpClient
        .Builder()
        .build()


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun providePicsumApi(retrofit: Retrofit): PicsumApi = retrofit.create(PicsumApi::class.java)

    @Singleton
    @Provides
    fun provideImageListDb(@ApplicationContext appContext: Context) =
        ImageListDb.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideImageListDao(db: ImageListDb) = db.getImageListDao()

    @Singleton
    @Provides
    fun provideRepository(
        picsumApi: PicsumApi,
        localDataSource: ImageListDao,
        imageListDb: ImageListDb
    ) = ImageListRepositoryImpl(picsumApi, localDataSource, imageListDb, MutableLiveData(-1))
}