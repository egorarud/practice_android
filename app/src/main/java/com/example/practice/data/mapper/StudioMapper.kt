package com.example.practice.data.mapper

import com.example.practice.data.dto.MoviesRefDto
import com.example.practice.data.dto.StudioDto
import com.example.practice.data.dto.StudioPageDto
import com.example.practice.domain.model.MoviesRef
import com.example.practice.domain.model.Studio
import com.example.practice.domain.model.StudioPage

fun StudioDto.toDomain(): Studio = Studio(
    id = id ?: "",
    subType = subType ?: "",
    title = title ?: "",
    type = type ?: "",
    movies = movies?.map { it.toDomain() } ?: emptyList(),
    updatedAt = updatedAt ?: "",
    createdAt = createdAt ?: ""
)

fun MoviesRefDto.toDomain(): MoviesRef = MoviesRef(
    id = id ?: 0
)

fun StudioPageDto.toDomain(): StudioPage = StudioPage(
    docs = docs.map { it.toDomain() },
    total = total,
    limit = limit,
    page = page,
    pages = pages
)
