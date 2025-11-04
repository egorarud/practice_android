package com.example.practice.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.data.cache.FilterBadgeCache
import com.example.practice.data.datastore.FilterPreferences
import com.example.practice.data.datastore.FilterSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val filterPreferences: FilterPreferences,
    private val filterBadgeCache: FilterBadgeCache
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FilterSettings())
    val uiState: StateFlow<FilterSettings> = _uiState.asStateFlow()
    
    init {
        loadFilters()
    }
    
    private fun loadFilters() {
        viewModelScope.launch {
            val settings = filterPreferences.getFilterSettings()
            _uiState.value = settings
            // Обновляем кэш после загрузки фильтров
            filterBadgeCache.updateFilterState(settings)
        }
    }
    
    fun saveFilters(
        searchQuery: String,
        type: String,
        minMoviesCount: Int,
        maxMoviesCount: Int
    ) {
        viewModelScope.launch {
            val settings = FilterSettings(
                searchQuery = searchQuery,
                type = type,
                minMoviesCount = minMoviesCount,
                maxMoviesCount = maxMoviesCount
            )
            filterPreferences.saveFilterSettings(settings)
            _uiState.value = settings
            // Обновляем кэш при сохранении фильтров
            filterBadgeCache.updateFilterState(settings)
        }
    }
}
