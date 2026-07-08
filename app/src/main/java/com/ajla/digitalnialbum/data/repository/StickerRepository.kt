package com.ajla.digitalnialbum.data.repository

import android.content.Context
import com.ajla.digitalnialbum.data.local.StickerDao
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.data.remote.ApiService
import com.ajla.digitalnialbum.data.remote.SafeJsonParser
import com.ajla.digitalnialbum.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StickerRepository(
    private val api: ApiService,
    private val dao: StickerDao,
    private val appContext: Context
) {

    companion object {
        const val PACKET_SIZE = 5
    }

    val allStickers: Flow<List<Sticker>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    val favoriteStickers: Flow<List<Sticker>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    fun observeSticker(id: Int): Flow<Sticker?> =
        dao.observeById(id).map { it?.toDomain() }

    suspend fun refreshCatalog(): Result<Unit> {
        if (!NetworkUtils.isOnline(appContext)) {
            return Result.failure(NoInternetException())
        }

        return try {
            val players = SafeJsonParser.parsePlayers(api.getAllPlayersRaw().string())
            val crests = SafeJsonParser.parseCrests(api.getAllCrestsRaw().string())

            val playerEntities = players.map { it.toEntity() }
            val crestEntities = crests.mapIndexed { index, crest ->
                crest.toEntity(numberInAlbum = players.size + index + 1)
            }
            val entities = playerEntities + crestEntities

            dao.insertIfAbsent(entities)

            entities.forEach { entity ->
                dao.updateCatalogFields(
                    id = entity.id,
                    name = entity.name,
                    category = entity.category,
                    rarity = entity.rarity,
                    imageUrl = entity.imageUrl,
                    description = entity.description,
                    position = entity.position,
                    jerseyNumber = entity.jerseyNumber,
                    numberInAlbum = entity.numberInAlbum
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun openPacket(): Result<List<Sticker>> {
        if (!NetworkUtils.isOnline(appContext)) {
            return Result.failure(NoInternetException())
        }

        return try {
            val rawResponse = api.openPacketRaw(PACKET_SIZE).string()
            val players = SafeJsonParser.parsePlayers(rawResponse)
            val crests = SafeJsonParser.parseCrestsFromPacket(rawResponse)
            val now = System.currentTimeMillis()
            val obtained = mutableListOf<Sticker>()

            players.forEach { player ->
                val existing = dao.getById(player.id)

                if (existing == null) {
                    dao.insertIfAbsent(listOf(player.toEntity()))
                }

                dao.markObtained(player.id, now)
                dao.getById(player.id)?.let { obtained.add(it.toDomain()) }
            }

            crests.forEach { crest ->
                val existingCatalogSize = dao.getTotalCount()
                val entity = crest.toEntity(numberInAlbum = existingCatalogSize + 1)
                val existing = dao.getById(entity.id)

                if (existing == null) {
                    dao.insertIfAbsent(listOf(entity))
                }

                dao.markObtained(entity.id, now)
                dao.getById(entity.id)?.let { obtained.add(it.toDomain()) }
            }

            Result.success(obtained)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(sticker: Sticker) {
        dao.setFavorite(sticker.id, !sticker.isFavorite)
    }
}

class NoInternetException : Exception("Nema internet konekcije")