package com.example.harmonyapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HarmonyGalleryApp : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: HarmonyGalleryApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}