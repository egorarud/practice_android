package com.example.practice.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.domain.model.Studio
import com.example.practice.domain.repository.StudiosRepository
import com.example.practice.data.repository.MockStudiosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel(
    private val repository: StudiosRepository = MockStudiosRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val _selectedItem = MutableStateFlow<Studio?>(null)
    val selectedItem: StateFlow<Studio?> = _selectedItem.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            repository.getStudiosPage(page = 1, limit = 50)
                .onSuccess { page ->
                    _uiState.value = ListUiState(
                        isLoading = false,
                        items = page.docs,
                        total = page.total
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false
                    )
                }
        }
    }

    fun selectItem(id: String) {
        viewModelScope.launch {
            repository.getStudioById(id)
                .onSuccess { studio ->
                    _selectedItem.value = studio
                }
                .onFailure { 
                    // Обработка ошибки
                }
        }
    }
}
