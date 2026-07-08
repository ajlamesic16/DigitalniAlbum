package com.ajla.digitalnialbum.ui.trade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajla.digitalnialbum.data.datastore.UserPreferencesRepository
import com.ajla.digitalnialbum.data.local.StickerDao
import com.ajla.digitalnialbum.data.model.Rarity
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.data.repository.StickerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TradeUiState(
    val duplicates: List<Sticker> = emptyList(),
    val missing: List<Sticker> = emptyList(),
    val tokens: Int = 0,
    val selectedTeam: String? = null,
    val teams: List<String> = emptyList()
)

class TradeViewModel(
    repository: StickerRepository,
    private val dao: StickerDao,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val selectedTeam = MutableStateFlow<String?>(null)

    private val _lastUnlocked = MutableStateFlow<Sticker?>(null)
    val lastUnlocked: StateFlow<Sticker?> = _lastUnlocked

    private val _unlockError = MutableStateFlow<String?>(null)
    val unlockError: StateFlow<String?> = _unlockError

    val uiState: StateFlow<TradeUiState> = combine(
        repository.allStickers,
        preferencesRepository.tokens,
        selectedTeam
    ) { stickers, tokens, team ->
        val filtered = stickers.filter { sticker ->
            team == null || sticker.category == team
        }

        TradeUiState(
            duplicates = filtered
                .filter { it.isOwned && it.quantity > 1 }
                .sortedWith(
                    compareByDescending<Sticker> { it.quantity }
                        .thenBy { it.numberInAlbum }
                ),
            missing = filtered
                .filter { !it.isOwned }
                .sortedBy { it.numberInAlbum },
            tokens = tokens,
            selectedTeam = team,
            teams = stickers.map { it.category }.distinct().sorted()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TradeUiState()
    )

    fun updateSelectedTeam(team: String?) {
        selectedTeam.value = team
    }

    fun costFor(rarity: Rarity): Int = when (rarity) {
        Rarity.COMMON -> 20
        Rarity.RARE -> 60
        Rarity.EPIC -> 150
        Rarity.LEGENDARY -> 300
    }

    fun exchangeDuplicate(sticker: Sticker) {
        viewModelScope.launch {
            dao.reduceQuantity(sticker.id)
            preferencesRepository.addTokens(sticker.rarity.tokenValue)
        }
    }

    fun unlockRarity(rarity: Rarity) {
        viewModelScope.launch {
            _unlockError.value = null
            val cost = costFor(rarity)
            val candidates = uiState.value.missing.filter { it.rarity == rarity }

            if (candidates.isEmpty()) {
                _unlockError.value = "Nema sličica te rijetkosti koje ti fale"
                return@launch
            }

            val success = preferencesRepository.spendTokens(cost)
            if (!success) {
                _unlockError.value = "Nemaš dovoljno tokena"
                return@launch
            }

            val target = candidates.random()
            dao.markObtained(target.id, System.currentTimeMillis())
            _lastUnlocked.value = target
        }
    }

    fun clearUnlockedMessage() {
        _lastUnlocked.value = null
    }

    fun clearError() {
        _unlockError.value = null
    }
}