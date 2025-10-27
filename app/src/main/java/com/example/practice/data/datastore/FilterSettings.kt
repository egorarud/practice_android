package com.example.practice.data.datastore

data class FilterSettings(
    val searchQuery: String = "",
    val minMoviesCount: Int = 0,
    val maxMoviesCount: Int = Int.MAX_VALUE,
    val type: String = "" // "Производство", "Спецэффекты" или пустая строка для всех
)
