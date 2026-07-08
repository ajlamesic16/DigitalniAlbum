package com.ajla.digitalnialbum.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.data.repository.StickerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StickerDetailViewModel(
    private val repository: StickerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stickerId: Int = checkNotNull(savedStateHandle["stickerId"])

    val sticker: StateFlow<Sticker?> = repository.observeSticker(stickerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun toggleFavorite() {
        val current = sticker.value ?: return
        viewModelScope.launch { repository.toggleFavorite(current) }
    }
}