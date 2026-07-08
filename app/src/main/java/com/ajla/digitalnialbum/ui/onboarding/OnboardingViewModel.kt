package com.ajla.digitalnialbum.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajla.digitalnialbum.data.datastore.UserPreferencesRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val availableTeams = listOf(
        "Argentina", "Austrija", "Belgija", "BiH", "Brazil", "Egipat",
        "Engleska", "Francuska", "Hrvatska", "Maroko", "Nizozemska",
        "Njemačka", "Norveska", "Portugal", "Turska"
    )

    fun completeOnboarding(selectedTeam: String, onDone: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.completeOnboarding(selectedTeam)
            onDone()
        }
    }
}