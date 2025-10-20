package com.example.practice.domain.repository

import com.example.practice.domain.model.Studio
import com.example.practice.domain.model.StudioPage

interface StudiosRepository {
    suspend fun getStudiosPage(page: Int = 1, limit: Int = 50): Result<StudioPage>
    suspend fun getStudioById(id: String): Result<Studio?>
}
