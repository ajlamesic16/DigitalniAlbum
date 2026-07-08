package com.ajla.digitalnialbum.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajla.digitalnialbum.data.datastore.UserPreferencesRepository
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.data.repository.StickerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    BY_NUMBER("Po broju"),
    BY_NAME("Po imenu")
}

data class AlbumUiState(
    val stickers: List<Sticker> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedTeam: String? = null,
    val favoriteTeam: String? = null,
    val sortOption: SortOption = SortOption.BY_NUMBER,
    val teams: List<String> = emptyList()
)

class AlbumViewModel(
    private val repository: StickerRepository,
    preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val selectedTeam = MutableStateFlow<String?>(null)
    private val sortOption = MutableStateFlow(SortOption.BY_NUMBER)
    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AlbumUiState> = combine(
        repository.allStickers,
        selectedTeam,
        sortOption,
        preferencesRepository.favoriteTeam
    ) { stickers, team, sort, favoriteTeam ->
        val filtered = stickers.filter { sticker ->
            team == null || sticker.category == team
        }

        val sorted = when (sort) {
            SortOption.BY_NUMBER -> filtered.sortedBy { it.numberInAlbum }
            SortOption.BY_NAME -> filtered.sortedBy { it.name }
        }

        val teams = stickers
            .map { it.category }
            .distinct()
            .sortedWith(
                compareByDescending<String> { it == favoriteTeam }
                    .thenBy { it }
            )

        AlbumUiState(
            stickers = sorted,
            isLoading = isLoading.value,
            errorMessage = errorMessage.value,
            selectedTeam = team,
            favoriteTeam = favoriteTeam,
            sortOption = sort,
            teams = teams
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AlbumUiState()
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true

            val result = repository.refreshCatalog()

            result.onFailure {
                errorMessage.value = it.message
            }

            result.onSuccess {
                errorMessage.value = null
            }

            isLoading.value = false
        }
    }

    fun updateSelectedTeam(team: String?) {
        selectedTeam.value = team
    }

    fun updateSortOption(option: SortOption) {
        sortOption.value = option
    }

    fun toggleFavorite(sticker: Sticker) {
        viewModelScope.launch {
            repository.toggleFavorite(sticker)
        }
    }
}