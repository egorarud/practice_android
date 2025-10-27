package com.example.practice.data.cache

import com.example.practice.data.datastore.FilterSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Кэш для хранения информации о необходимости показа бейджа фильтров.
 * Используется для отображения индикатора, что фильтры применены.
 */
@Singleton
class FilterBadgeCache @Inject constructor() {
    
    private val _hasActiveFilters = MutableStateFlow<Boolean>(false)
    val hasActiveFilters: StateFlow<Boolean> = _hasActiveFilters.asStateFlow()
    
    /**
     * Проверяет, применены ли фильтры (не в дефолтном состоянии)
     */
    fun isFilterActive(filterSettings: FilterSettings): Boolean {
        val hasSearchQuery = filterSettings.searchQuery.isNotBlank()
        val hasType = filterSettings.type.isNotBlank()
        val hasMinMovies = filterSettings.minMoviesCount > 0
        val hasMaxMovies = filterSettings.maxMoviesCount < Int.MAX_VALUE
        
        return hasSearchQuery || hasType || hasMinMovies || hasMaxMovies
    }
    
    /**
     * Обновляет состояние кэша на основе текущих настроек фильтров
     */
    fun updateFilterState(filterSettings: FilterSettings) {
        _hasActiveFilters.value = isFilterActive(filterSettings)
    }
    
    /**
     * Получить текущее состояние
     */
    fun getHasActiveFilters(): Boolean {
        return _hasActiveFilters.value
    }
}

