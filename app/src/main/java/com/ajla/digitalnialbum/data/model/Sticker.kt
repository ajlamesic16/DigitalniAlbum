package com.ajla.digitalnialbum.data.model

data class Sticker(
    val id: Int,
    val name: String,
    val category: String,
    val rarity: Rarity,
    val imageUrl: String,
    val description: String,
    val position: String?,
    val stickerType: String,
    val jerseyNumber: Int?,
    val numberInAlbum: Int,
    val isOwned: Boolean,
    val quantity: Int,
    val isFavorite: Boolean,
    val dateObtained: Long?
)

enum class Rarity(val label: String, val tokenValue: Int) {
    COMMON("Obična", 5),
    RARE("Rijetka", 15),
    EPIC("Epska", 40),
    LEGENDARY("Legendarna", 100);

    companion object {
        fun deriveFromId(id: Int): Rarity {
            val bucket = (id * 7 + 3) % 10

            return when (bucket) {
                0 -> LEGENDARY
                in 1..2 -> EPIC
                in 3..5 -> RARE
                else -> COMMON
            }
        }
    }
}