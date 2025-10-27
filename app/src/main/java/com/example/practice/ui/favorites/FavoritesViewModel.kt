package com.example.practice.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.domain.model.Studio
import com.example.practice.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.getAllFavorites().collect { favorites ->
                _uiState.value = _uiState.value.copy(
                    items = favorites,
                    total = favorites.size
                )
            }
        }
    }
    
    fun removeFromFavorites(studioId: String) {
        viewModelScope.launch {
            try {
                favoritesRepository.removeFromFavorites(studioId)
                // Состояние обновится автоматически через Flow
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Не удалось удалить из избранного"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class FavoritesUiState(
    val items: List<Studio> = emptyList(),
    val total: Int = 0,
    val error: String? = null
)
