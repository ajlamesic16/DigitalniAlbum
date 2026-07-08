package com.ajla.digitalnialbum.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajla.digitalnialbum.data.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val displayMode: StateFlow<String> = preferencesRepository.displayMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "LIST")

    fun setDisplayMode(mode: String) {
        viewModelScope.launch { preferencesRepository.setDisplayMode(mode) }
    }
}