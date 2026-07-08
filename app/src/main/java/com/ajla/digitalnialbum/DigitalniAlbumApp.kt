package com.ajla.digitalnialbum

import android.app.Application
import com.ajla.digitalnialbum.di.AppContainer

class DigitalniAlbumApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}