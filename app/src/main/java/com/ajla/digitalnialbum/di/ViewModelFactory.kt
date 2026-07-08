package com.ajla.digitalnialbum.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ajla.digitalnialbum.DigitalniAlbumApp
import com.ajla.digitalnialbum.ui.album.AlbumViewModel
import com.ajla.digitalnialbum.ui.detail.StickerDetailViewModel
import com.ajla.digitalnialbum.ui.favorites.FavoritesViewModel
import com.ajla.digitalnialbum.ui.onboarding.OnboardingViewModel
import com.ajla.digitalnialbum.ui.packet.PacketViewModel
import com.ajla.digitalnialbum.ui.settings.SettingsViewModel
import com.ajla.digitalnialbum.ui.statistics.StatisticsViewModel
import com.ajla.digitalnialbum.ui.trade.TradeViewModel

val AppViewModelFactory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DigitalniAlbumApp
        OnboardingViewModel(app.container.userPreferencesRepository)
    }

    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DigitalniAlbumApp
        AlbumViewModel(
            repository = app.container.stickerRepository,
            preferencesRepository = app.container.userPreferencesRepository
        )
    }

    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DigitalniAlbumApp
        StickerDetailViewModel(
            repository = app.container.stickerRepository,
            savedStateHandle = this.createSavedStateHandle()
        )
    }

    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DigitalniAlbumApp
        FavoritesViewModel(app.container.stickerRepository)
    }

    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DigitalniAlbumApp
        PacketViewModel(
            repository = app.container.stickerRepository,
            preferencesRepository = app.container.userPreferencesRepository
        )
    }

    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DigitalniAlbumApp
        StatisticsViewModel(app.container.stickerRepository)
    }

    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DigitalniAlbumApp
        SettingsViewModel(app.container.userPreferencesRepository)
    }

    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DigitalniAlbumApp
        TradeViewModel(
            repository = app.container.stickerRepository,
            dao = app.container.stickerDao,
            preferencesRepository = app.container.userPreferencesRepository
        )
    }
}