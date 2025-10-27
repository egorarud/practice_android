package com.example.practice.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    private val searchQueryKey = stringPreferencesKey("search_query")
    private val minMoviesCountKey = intPreferencesKey("min_movies_count")
    private val maxMoviesCountKey = intPreferencesKey("max_movies_count")
    private val typeKey = stringPreferencesKey("type")
    
    val filterSettings: Flow<FilterSettings> = dataStore.data.map { preferences ->
        FilterSettings(
            searchQuery = preferences[searchQueryKey] ?: "",
            minMoviesCount = preferences[minMoviesCountKey] ?: 0,
            maxMoviesCount = preferences[maxMoviesCountKey] ?: Int.MAX_VALUE,
            type = preferences[typeKey] ?: ""
        )
    }
    
    suspend fun saveFilterSettings(settings: FilterSettings) {
        dataStore.edit { preferences ->
            preferences[searchQueryKey] = settings.searchQuery
            preferences[minMoviesCountKey] = settings.minMoviesCount
            preferences[maxMoviesCountKey] = settings.maxMoviesCount
            preferences[typeKey] = settings.type
        }
    }
    
    suspend fun getFilterSettings(): FilterSettings {
        return filterSettings.first()
    }
}
