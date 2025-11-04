package com.example.practice.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.data.cache.FilterBadgeCache
import com.example.practice.domain.model.Studio
import com.example.practice.domain.usecase.GetStudioByIdUseCase
import com.example.practice.domain.usecase.GetStudiosPageUseCase
import com.example.practice.data.datastore.FilterPreferences
import com.example.practice.data.datastore.FilterSettings
import com.example.practice.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkListViewModel @Inject constructor(
    private val getStudiosPageUseCase: GetStudiosPageUseCase,
    private val getStudioByIdUseCase: GetStudioByIdUseCase,
    private val filterPreferences: FilterPreferences,
    private val favoritesRepository: FavoritesRepository,
    private val filterBadgeCache: FilterBadgeCache
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()
    
    private val _detailState = MutableStateFlow(DetailUiState())
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()
    
    private val _filters = MutableStateFlow(FilterSettings())
    val filters: StateFlow<FilterSettings> = _filters.asStateFlow()
    
    private var isInitialized = false
    
    init {
        loadFiltersAndItems()
    }
    
    private fun loadFiltersAndItems() {
        viewModelScope.launch {
            filterPreferences.filterSettings.collect { settings ->
                val previousSettings = _filters.value
                _filters.value = settings
                
                // Обновляем кэш бейджа
                filterBadgeCache.updateFilterState(settings)
                
                // При первом запуске загружаем данные один раз
                if (!isInitialized) {
                    isInitialized = true
                    loadItems()
                } else if (previousSettings != settings) {
                    // При изменении фильтров перезагружаем список
                    loadItems()
                }
            }
        }
    }
    
    fun loadItems(page: Int = 1) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            getStudiosPageUseCase(page = page, limit = 50)
                .onSuccess { studioPage ->
                    // Применяем фильтры к загруженным данным
                    val filteredDocs = applyFilters(studioPage.docs)
                    
                    _uiState.value = ListUiState(
                        isLoading = false,
                        items = filteredDocs,
                        total = filteredDocs.size,
                        currentPage = studioPage.page,
                        hasMorePages = studioPage.page < studioPage.pages,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Не удалось загрузить список студий"
                    )
                }
        }
    }
    
    private fun applyFilters(items: List<Studio>): List<Studio> {
        val filters = _filters.value
        return items.filter { studio ->
            // Фильтр по поисковому запросу
            val matchesSearch = filters.searchQuery.isEmpty() || 
                    studio.title.contains(filters.searchQuery, ignoreCase = true)
            
            // Фильтр по типу студии
            val matchesType = filters.type.isEmpty() || 
                    studio.type == filters.type
            
            // Фильтр по количеству фильмов
            val matchesMoviesCount = studio.movies.size >= filters.minMoviesCount && 
                    studio.movies.size <= filters.maxMoviesCount
            
            matchesSearch && matchesType && matchesMoviesCount
        }
    }
    
    fun loadMoreItems() {
        val currentState = _uiState.value
        if (!currentState.hasMorePages || currentState.isLoading) return
        
        viewModelScope.launch {
            getStudiosPageUseCase(page = currentState.currentPage + 1, limit = 50)
                .onSuccess { studioPage ->
                    // Применяем фильтры к новым данным
                    val filteredDocs = applyFilters(studioPage.docs)
                    
                    _uiState.value = currentState.copy(
                        items = currentState.items + filteredDocs,
                        currentPage = studioPage.page,
                        hasMorePages = studioPage.page < studioPage.pages,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = exception.message ?: "Не удалось загрузить дополнительные данные"
                    )
                }
        }
    }
    
    fun refreshItems() {
        loadItems(page = 1)
    }
    
    fun selectItem(id: String) {
        _detailState.value = DetailUiState(isLoading = true, error = null)
        
        viewModelScope.launch {
            getStudioByIdUseCase(id)
                .onSuccess { studio ->
                    _detailState.value = DetailUiState(
                        isLoading = false,
                        studio = studio,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _detailState.value = DetailUiState(
                        isLoading = false,
                        studio = null,
                        error = exception.message ?: "Не удалось загрузить данные студии"
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        _detailState.value = _detailState.value.copy(error = null)
    }
    
    suspend fun addToFavorites(studioId: String) {
        val studio = uiState.value.items.find { it.id == studioId }
            ?: _detailState.value.studio
        
        studio?.let {
            favoritesRepository.addToFavorites(it)
        }
    }
}
