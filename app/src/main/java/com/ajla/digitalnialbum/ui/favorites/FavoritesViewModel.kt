package com.ajla.digitalnialbum.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.data.repository.StickerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FavoriteSortOption(val label: String) {
    BY_NUMBER("Po broju"),
    BY_NAME("Po imenu")
}

data class FavoritesUiState(
    val favorites: List<Sticker> = emptyList(),
    val selectedTeam: String? = null,
    val teams: List<String> = emptyList(),
    val sortOption: FavoriteSortOption = FavoriteSortOption.BY_NUMBER
)

class FavoritesViewModel(
    private val repository: StickerRepository
) : ViewModel() {

    private val selectedTeam = MutableStateFlow<String?>(null)
    private val sortOption = MutableStateFlow(FavoriteSortOption.BY_NUMBER)

    val uiState: StateFlow<FavoritesUiState> = combine(
        repository.favoriteStickers,
        selectedTeam,
        sortOption
    ) { favorites, team, sort ->
        val filtered = favorites.filter { sticker ->
            team == null || sticker.category == team
        }

        val sorted = when (sort) {
            FavoriteSortOption.BY_NUMBER -> filtered.sortedBy { it.numberInAlbum }
            FavoriteSortOption.BY_NAME -> filtered.sortedBy { it.name }
        }

        FavoritesUiState(
            favorites = sorted,
            selectedTeam = team,
            teams = favorites.map { it.category }.distinct().sorted(),
            sortOption = sort
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FavoritesUiState()
    )

    fun updateSelectedTeam(team: String?) {
        selectedTeam.value = team
    }

    fun updateSortOption(option: FavoriteSortOption) {
        sortOption.value = option
    }

    fun toggleFavorite(sticker: Sticker) {
        viewModelScope.launch {
            repository.toggleFavorite(sticker)
        }
    }
}