package com.example.practice.domain.model

data class Studio(
    val id: String,
    val subType: String,
    val title: String,
    val type: String,
    val movies: List<MoviesRef>,
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
