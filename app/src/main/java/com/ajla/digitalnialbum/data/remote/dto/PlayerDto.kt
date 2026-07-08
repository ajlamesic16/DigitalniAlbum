package com.ajla.digitalnialbum.data.remote.dto

data class PlayerDto(
    val id: Int,
    val ime: String,
    val prezime: String,
    val brojDresa: Int?,
    val reprezentacija: String,
    val pozicija: String?,
    val slicicaLokacija: String?,
    val stickerType: String = "player"
)