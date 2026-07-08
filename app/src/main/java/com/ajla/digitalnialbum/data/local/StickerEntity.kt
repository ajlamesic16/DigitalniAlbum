package com.ajla.digitalnialbum.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stickers")
data class StickerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val rarity: String,
    val imageUrl: String = "",
    val description: String,
    val stickerType: String = "player",
    val position: String? = null,
    val jerseyNumber: Int? = null,
    val numberInAlbum: Int,
    val isOwned: Boolean = false,
    val quantity: Int = 0,
    val isFavorite: Boolean = false,
    val dateObtained: Long? = null
)