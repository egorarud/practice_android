package com.example.practice.domain.usecase

import com.example.practice.domain.model.Studio
import com.example.practice.domain.model.StudioPage
import com.example.practice.domain.repository.StudiosRepository
import javax.inject.Inject

class GetStudiosPageUseCase @Inject constructor(
    private val repository: StudiosRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 50): Result<StudioPage> {
        return repository.getStudiosPage(page, limit)
    }
}

class GetStudioByIdUseCase @Inject constructor(
    private val repository: StudiosRepository
) {
    suspend operator fun invoke(id: String): Result<Studio?> {
        return repository.getStudioById(id)
    }
}
