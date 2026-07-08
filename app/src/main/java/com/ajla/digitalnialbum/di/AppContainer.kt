package com.ajla.digitalnialbum.di

import android.content.Context
import com.ajla.digitalnialbum.data.datastore.UserPreferencesRepository
import com.ajla.digitalnialbum.data.local.AppDatabase
import com.ajla.digitalnialbum.data.remote.RetrofitInstance
import com.ajla.digitalnialbum.data.repository.StickerRepository

class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val stickerDao = database.stickerDao()

    val userPreferencesRepository = UserPreferencesRepository(context)

    val stickerRepository = StickerRepository(
        api = RetrofitInstance.api,
        dao = stickerDao,
        appContext = context.applicationContext
    )
}