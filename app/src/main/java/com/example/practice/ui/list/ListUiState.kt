package com.example.practice.ui.list

import com.example.practice.domain.model.Studio

data class ListUiState(
    val isLoading: Boolean = false,
    val items: List<Studio> = emptyList(),
    val total: Int = 0,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = false
)

data class DetailUiState(
    val isLoading: Boolean = false,
    val studio: Studio? = null,
    val error: String? = null
)

