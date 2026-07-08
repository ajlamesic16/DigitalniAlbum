package com.ajla.digitalnialbum.ui.statistics

import androidx.lifecycle.ViewModel
import com.ajla.digitalnialbum.data.model.Rarity
import com.ajla.digitalnialbum.data.repository.StickerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

data class StatisticsUiState(
    val ownedCount: Int = 0,
    val totalCount: Int = 0,
    val commonCount: Int = 0,
    val rareCount: Int = 0,
    val epicCount: Int = 0,
    val legendaryCount: Int = 0,
    val missingCount: Int = 0,
    val duplicateCount: Int = 0
) {
    val percentage: Float
        get() = if (totalCount == 0) 0f else ownedCount.toFloat() / totalCount.toFloat()
}

class StatisticsViewModel(
    repository: StickerRepository
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = repository.allStickers
        .map { stickers ->
            val owned = stickers.filter { it.isOwned }

            StatisticsUiState(
                ownedCount = owned.size,
                totalCount = stickers.size,
                commonCount = owned.count { it.rarity == Rarity.COMMON },
                rareCount = owned.count { it.rarity == Rarity.RARE },
                epicCount = owned.count { it.rarity == Rarity.EPIC },
                legendaryCount = owned.count { it.rarity == Rarity.LEGENDARY },
                missingCount = stickers.count { !it.isOwned },
                duplicateCount = stickers.sumOf { sticker ->
                    if (sticker.quantity > 1) sticker.quantity - 1 else 0
                }
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            StatisticsUiState()
        )
}