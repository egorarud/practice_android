package com.example.practice.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.practice.domain.model.Profile
import com.example.practice.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    @Named("profile_prefs") private val dataStore: DataStore<Preferences>
) : ProfileRepository {

    private object Keys {
        val FULL_NAME = stringPreferencesKey("full_name")
        val AVATAR_URI = stringPreferencesKey("avatar_uri")
        val RESUME_URL = stringPreferencesKey("resume_url")
        val POSITION = stringPreferencesKey("position")
    }

    override val profileFlow: Flow<Profile> = dataStore.data.map { prefs ->
        Profile(
            fullName = prefs[Keys.FULL_NAME] ?: "",
            avatarUri = prefs[Keys.AVATAR_URI] ?: "",
            resumeUrl = prefs[Keys.RESUME_URL] ?: "",
            position = prefs[Keys.POSITION] ?: ""
        )
    }

    override suspend fun saveProfile(profile: Profile) {
        dataStore.edit { prefs ->
            prefs[Keys.FULL_NAME] = profile.fullName
            prefs[Keys.AVATAR_URI] = profile.avatarUri
            prefs[Keys.RESUME_URL] = profile.resumeUrl
            prefs[Keys.POSITION] = profile.position
        }
    }
}


