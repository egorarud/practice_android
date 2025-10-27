package com.example.practice.data.repository

import com.example.practice.data.database.dao.FavoriteStudioDao
import com.example.practice.data.database.entity.FavoriteStudio
import com.example.practice.domain.model.Studio
import com.example.practice.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteStudioDao: FavoriteStudioDao
) : FavoritesRepository {
    
    override fun getAllFavorites(): Flow<List<Studio>> {
        return favoriteStudioDao.getAllFavorites().map { favoriteStudios ->
            favoriteStudios.map { it.toDomain() }
        }
    }
    
    override suspend fun addToFavorites(studio: Studio) {
        val favoriteStudio = FavoriteStudio(
            id = studio.id,
            subType = studio.subType,
            title = studio.title,
            type = studio.type,
            moviesCount = studio.movies.size,
            updatedAt = studio.updatedAt,
            createdAt = studio.createdAt
        )
        favoriteStudioDao.insertFavorite(favoriteStudio)
    }
    
    override suspend fun removeFromFavorites(studioId: String) {
        favoriteStudioDao.deleteFavoriteById(studioId)
    }
    
    override suspend fun isFavorite(studioId: String): Boolean {
        return favoriteStudioDao.isFavorite(studioId)
    }
    
    override suspend fun getFavoritesCount(): Int {
        return favoriteStudioDao.getFavoritesCount()
    }
    
    private fun FavoriteStudio.toDomain(): Studio {
        return Studio(
            id = id,
            subType = subType,
            title = title,
            type = type,
            movies = List(moviesCount) { index ->
                com.example.practice.domain.model.MoviesRef(id = index)
            },
            updatedAt = updatedAt,
            createdAt = createdAt
        )
    }
}
