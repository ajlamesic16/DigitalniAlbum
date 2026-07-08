package com.ajla.digitalnialbum.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerDao {

    @Query("SELECT * FROM stickers ORDER BY numberInAlbum ASC")
    fun observeAll(): Flow<List<StickerEntity>>

    @Query("SELECT * FROM stickers WHERE isFavorite = 1 ORDER BY numberInAlbum ASC")
    fun observeFavorites(): Flow<List<StickerEntity>>

    @Query("SELECT * FROM stickers WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<StickerEntity?>

    @Query("SELECT * FROM stickers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): StickerEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(stickers: List<StickerEntity>): List<Long>

    @Query(
        """
        UPDATE stickers SET
            name = :name,
            category = :category,
            rarity = :rarity,
            imageUrl = :imageUrl,
            description = :description,
            position = :position,
            jerseyNumber = :jerseyNumber,
            numberInAlbum = :numberInAlbum
        WHERE id = :id
        """
    )
    suspend fun updateCatalogFields(
        id: Int,
        name: String,
        category: String,
        rarity: String,
        imageUrl: String,
        description: String,
        position: String?,
        jerseyNumber: Int?,
        numberInAlbum: Int
    )

    @Query("UPDATE stickers SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)

    @Query(
        """
        UPDATE stickers SET
            isOwned = 1,
            quantity = quantity + 1,
            dateObtained = CASE WHEN dateObtained IS NULL THEN :now ELSE dateObtained END
        WHERE id = :id
        """
    )
    suspend fun markObtained(id: Int, now: Long)

    @Query("UPDATE stickers SET quantity = quantity - 1 WHERE id = :id AND quantity > 1")
    suspend fun reduceQuantity(id: Int)

    @Query("SELECT COUNT(*) FROM stickers WHERE isOwned = 1")
    fun observeOwnedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM stickers")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM stickers")
    suspend fun getTotalCount(): Int
}