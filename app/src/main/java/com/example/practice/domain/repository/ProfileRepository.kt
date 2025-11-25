package com.example.practice.domain.repository

import com.example.practice.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profileFlow: Flow<Profile>
    suspend fun saveProfile(profile: Profile)
}



