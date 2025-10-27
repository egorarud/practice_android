package com.example.practice.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.example.practice.data.database.entity.FavoriteStudio
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStudioDao {
    
    @Query("SELECT * FROM favorite_studios ORDER BY addedToFavoritesAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteStudio>>
    
    @Query("SELECT * FROM favorite_studios WHERE id = :id")
    suspend fun getFavoriteById(id: String): FavoriteStudio?
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_studios WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteStudio: FavoriteStudio)
    
    @Delete
    suspend fun deleteFavorite(favoriteStudio: FavoriteStudio)
    
    @Query("DELETE FROM favorite_studios WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)
    
    @Query("SELECT COUNT(*) FROM favorite_studios")
    suspend fun getFavoritesCount(): Int
}
