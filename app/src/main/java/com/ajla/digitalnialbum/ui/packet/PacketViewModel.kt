package com.ajla.digitalnialbum.ui.packet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajla.digitalnialbum.data.datastore.UserPreferencesRepository
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.data.repository.StickerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PacketState {
    data object Waiting : PacketState
    data object Opening : PacketState
    data class Revealed(val stickers: List<Sticker>) : PacketState
    data class Error(val message: String) : PacketState
}

class PacketViewModel(
    private val repository: StickerRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PacketState>(PacketState.Waiting)
    val state: StateFlow<PacketState> = _state

    val remainingPacketsToday: StateFlow<Int> = preferencesRepository.remainingPacketsToday
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun openPacket() {
        if (_state.value is PacketState.Opening) return
        if (remainingPacketsToday.value <= 0) {
            _state.value = PacketState.Error("Iskoristila si sve paketiće za danas. Dođi sutra!")
            return
        }
        _state.value = PacketState.Opening
        viewModelScope.launch {
            val result = repository.openPacket()
            result.onSuccess { stickers ->
                preferencesRepository.recordPacketOpened()
                _state.value = PacketState.Revealed(stickers)
            }
            result.onFailure { error -> _state.value = PacketState.Error(error.message ?: "Greška") }
        }
    }

    fun reset() {
        _state.value = PacketState.Waiting
    }
}