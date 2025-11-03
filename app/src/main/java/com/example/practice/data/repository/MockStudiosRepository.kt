package com.example.practice.data.repository

import com.example.practice.domain.model.MoviesRef
import com.example.practice.domain.model.Studio
import com.example.practice.domain.model.StudioPage
import com.example.practice.domain.repository.StudiosRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockStudiosRepository @Inject constructor() : StudiosRepository {
    
    private val studios: List<Studio> = List(100) { index ->
        val id = "studio-${index + 1}"
        Studio(
            id = id,
            subType = if (index % 2 == 0) "Киностудия" else "Анимационная",
            title = "Студия #${index + 1}",
            type = "Производство",
            movies = listOf(MoviesRef(id = (index % 10))),
            updatedAt = "2025-10-06T17:35:19.352Z",
            createdAt = "2025-10-01T09:00:00.000Z"
        )
    }

    override suspend fun getStudiosPage(page: Int, limit: Int): Result<StudioPage> {
        delay(500) // Имитация сетевой задержки
        
        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, studios.size)
        val paginatedStudios = studios.subList(startIndex, endIndex)
        
        return Result.success(
            StudioPage(
                docs = paginatedStudios,
                total = studios.size,
                limit = limit,
                page = page,
                pages = (studios.size + limit - 1) / limit
            )
        )
    }

    override suspend fun getStudioById(id: String): Result<Studio?> {
        delay(300) // Имитация сетевой задержки
        
        val studio = studios.firstOrNull { it.id == id }
        return if (studio != null) {
            Result.success(studio)
        } else {
            Result.failure(Exception("Студия с ID '$id' не найдена."))
        }
    }
}

