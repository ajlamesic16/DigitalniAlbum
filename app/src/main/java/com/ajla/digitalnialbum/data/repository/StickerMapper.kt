package com.ajla.digitalnialbum.data.repository

import com.ajla.digitalnialbum.data.local.StickerEntity
import com.ajla.digitalnialbum.data.model.Rarity
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.data.remote.RetrofitInstance
import com.ajla.digitalnialbum.data.remote.dto.CrestDto
import com.ajla.digitalnialbum.data.remote.dto.PlayerDto

fun PlayerDto.toEntity(): StickerEntity {
    val basePath = RetrofitInstance.BASE_URL.trimEnd('/')
    val relativePath = slicicaLokacija?.let { if (it.startsWith("/")) it else "/$it" }
        ?: "/api/slicica/$id"
    val stickerImageUrl = basePath + relativePath

    return StickerEntity(
        id = id,
        name = "$ime $prezime".trim(),
        category = reprezentacija,
        rarity = Rarity.deriveFromId(id).name,
        imageUrl = stickerImageUrl,
        description = buildString {
            append("Reprezentacija: $reprezentacija")
            pozicija?.let { append(" • Pozicija: $it") }
            brojDresa?.let { append(" • Broj dresa: $it") }
        },
        position = pozicija,
        stickerType = "player",
        jerseyNumber = brojDresa,
        numberInAlbum = id
    )
}

fun CrestDto.toEntity(numberInAlbum: Int): StickerEntity {
    val basePath = RetrofitInstance.BASE_URL.trimEnd('/')
    val relativePath = if (slicicaLokacija.startsWith("/")) {
        slicicaLokacija
    } else {
        "/$slicicaLokacija"
    }
    val crestImageUrl = basePath + relativePath

    val numericId = 900000 + kotlin.math.abs(id.hashCode() % 100000)

    return StickerEntity(
        id = numericId,
        name = "Grb - $reprezentacija",
        category = reprezentacija,
        rarity = Rarity.RARE.name,
        imageUrl = crestImageUrl,
        description = "Grb reprezentacije $reprezentacija",
        position = null,
        stickerType = "grb",
        jerseyNumber = null,
        numberInAlbum = numberInAlbum
    )
}

fun StickerEntity.toDomain(): Sticker = Sticker(
    id = id,
    name = name,
    category = category,
    rarity = runCatching { Rarity.valueOf(rarity) }.getOrDefault(Rarity.COMMON),
    imageUrl = imageUrl,
    description = description,
    position = position,
    stickerType = stickerType,
    jerseyNumber = jerseyNumber,
    numberInAlbum = numberInAlbum,
    isOwned = isOwned,
    quantity = quantity,
    isFavorite = isFavorite,
    dateObtained = dateObtained
)