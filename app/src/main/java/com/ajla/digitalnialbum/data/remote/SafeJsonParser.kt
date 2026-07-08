package com.ajla.digitalnialbum.data.remote

import com.ajla.digitalnialbum.data.remote.dto.CrestDto
import com.ajla.digitalnialbum.data.remote.dto.PlayerDto
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object SafeJsonParser {
    fun parsePlayers(json: String): List<PlayerDto> {
        val array = safeParseArray(json) ?: return emptyList()
        return array.mapNotNull { element ->
            runCatching { parsePlayerElement(element.asJsonObject) }.getOrNull()
        }
    }

    fun parseCrests(json: String): List<CrestDto> {
        val array = safeParseArray(json) ?: return emptyList()
        return array.mapNotNull { element ->
            runCatching { parseCrestElement(element.asJsonObject) }.getOrNull()
        }
    }

    fun parseCrestsFromPacket(json: String): List<CrestDto> {
        val array = safeParseArray(json) ?: return emptyList()
        return array.mapNotNull { element ->
            runCatching {
                val obj = element.asJsonObject
                val tip = obj.readString("tip_slicice")
                if (tip != "grb") return@runCatching null
                val id = obj.readString("id") ?: return@runCatching null
                val reprezentacija = obj.readString("reprezentacija") ?: return@runCatching null
                val slicicaLokacija = obj.readString("slicica_lokacija") ?: ""
                CrestDto(id = id, reprezentacija = reprezentacija, slicicaLokacija = slicicaLokacija)
            }.getOrNull()
        }
    }

    private fun safeParseArray(json: String): JsonArray? =
        runCatching { JsonParser.parseString(json).asJsonArray }.getOrNull()

    private fun parsePlayerElement(obj: JsonObject): PlayerDto? {
        val tipSlicice = obj.readString("tip_slicice") ?: "player"
        if (tipSlicice == "grb") return null

        val id = obj.readInt("id") ?: return null
        val ime = obj.readString("ime") ?: return null
        val reprezentacija = obj.readString("reprezentacija") ?: return null
        val prezime = obj.readString("prezime") ?: ""
        val brojDresa = obj.readInt("broj_dresa")
        val pozicija = obj.readString("pozicija")
        val slicicaLokacija = obj.readString("slicica_lokacija")

        return PlayerDto(
            id = id,
            ime = ime,
            prezime = prezime,
            brojDresa = brojDresa,
            reprezentacija = reprezentacija,
            pozicija = pozicija,
            slicicaLokacija = slicicaLokacija,
            stickerType = tipSlicice
        )
    }

    private fun parseCrestElement(obj: JsonObject): CrestDto? {
        val id = obj.readString("id") ?: return null
        val reprezentacija = obj.readString("reprezentacija") ?: return null
        val slicicaLokacija = obj.readString("slicica_lokacija") ?: ""
        return CrestDto(id = id, reprezentacija = reprezentacija, slicicaLokacija = slicicaLokacija)
    }

    private fun JsonObject.readString(key: String): String? {
        val el = this.get(key) ?: return null
        if (el.isJsonNull) return null
        return runCatching { el.asString }.getOrNull()?.takeIf { it.isNotBlank() }
    }

    private fun JsonObject.readInt(key: String): Int? {
        val el = this.get(key) ?: return null
        if (el.isJsonNull) return null
        return runCatching {
            if (el.isJsonPrimitive && el.asJsonPrimitive.isNumber) {
                el.asInt
            } else {
                el.asString.toIntOrNull()
            }
        }.getOrNull()
    }
}