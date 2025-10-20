package com.example.practice.data.dto

import com.google.gson.annotations.SerializedName

data class StudioDto(
    @SerializedName("id")
    val id: String?,
    
    @SerializedName("subType")
    val subType: String?,
    
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("type")
    val type: String?,
    
    @SerializedName("movies")
    val movies: List<MoviesRefDto>?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?
)

data class MoviesRefDto(
    @SerializedName("id")
    val id: Int?
)

data class StudioPageDto(
    @SerializedName("docs")
    val docs: List<StudioDto>,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("limit")
    val limit: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("pages")
    val pages: Int
)
