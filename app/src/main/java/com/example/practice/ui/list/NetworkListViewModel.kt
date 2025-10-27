package com.example.practice.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.domain.model.Studio
import com.example.practice.domain.usecase.GetStudioByIdUseCase
import com.example.practice.domain.usecase.GetStudiosPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkListViewModel @Inject constructor(
    private val getStudiosPageUseCase: GetStudiosPageUseCase,
    private val getStudioByIdUseCase: GetStudioByIdUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()
    
    private val _detailState = MutableStateFlow(DetailUiState())
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()
    
    init {
        loadItems()
    }
    
    fun loadItems(page: Int = 1) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            getStudiosPageUseCase(page = page, limit = 50)
                .onSuccess { studioPage ->
                    _uiState.value = ListUiState(
                        isLoading = false,
                        items = studioPage.docs,
                        total = studioPage.total,
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
    
    fun loadMoreItems() {
        val currentState = _uiState.value
        if (!currentState.hasMorePages || currentState.isLoading) return
        
        viewModelScope.launch {
            getStudiosPageUseCase(page = currentState.currentPage + 1, limit = 50)
                .onSuccess { studioPage ->
                    _uiState.value = currentState.copy(
                        items = currentState.items + studioPage.docs,
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
}
