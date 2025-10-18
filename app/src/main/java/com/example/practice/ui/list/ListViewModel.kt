package com.example.practice.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Studio(
    val id: String,
    val subType: String,
    val title: String,
    val type: String, // например, "Производство"
    val movies: MoviesRef,
    val updatedAt: String,
    val createdAt: String
)

data class MoviesRef(
    val id: Int
)

data class StudioPage(
    val docs: List<Studio>,
    val total: Int,
    val limit: Int,
    val page: Int,
    val pages: Int
)

interface StudiosRepository {
    fun getStudiosPage(): StudioPage
    fun getStudioById(id: String): Studio?
}

class MockStudiosRepository : StudiosRepository {
    private val studios: List<Studio> = List(100) { index ->
        val id = "studio-${index + 1}"
        Studio(
            id = id,
            subType = if (index % 2 == 0) "Киностудия" else "Анимационная",
            title = "Студия #${index + 1}",
            type = "Производство",
            movies = MoviesRef(id = (index % 10)),
            updatedAt = "2025-10-06T17:35:19.352Z",
            createdAt = "2025-10-01T09:00:00.000Z"
        )
    }

    override fun getStudiosPage(): StudioPage = StudioPage(
        docs = studios,
        total = studios.size,
        limit = 50,
        page = 1,
        pages = (studios.size + 49) / 50
    )

    override fun getStudioById(id: String): Studio? = studios.firstOrNull { it.id == id }
}

data class ListUiState(
    val isLoading: Boolean = false,
    val items: List<Studio> = emptyList(),
    val total: Int = 0
)

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
            val page = repository.getStudiosPage()
            _uiState.value = ListUiState(isLoading = false, items = page.docs, total = page.total)
        }
    }

    fun selectItem(id: String) {
        _selectedItem.value = repository.getStudioById(id)
    }
}


