package com.example.practice.domain.repository

import com.example.practice.domain.model.Studio
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(): Flow<List<Studio>>
    suspend fun addToFavorites(studio: Studio)
    suspend fun removeFromFavorites(studioId: String)
    suspend fun isFavorite(studioId: String): Boolean
    suspend fun getFavoritesCount(): Int
}
