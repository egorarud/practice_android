package com.example.practice.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_studios")
data class FavoriteStudio(
    @PrimaryKey
    val id: String,
    val subType: String,
    val title: String,
    val type: String,
    val moviesCount: Int,
    val updatedAt: String,
    val createdAt: String,
    val addedToFavoritesAt: Long = System.currentTimeMillis()
)
