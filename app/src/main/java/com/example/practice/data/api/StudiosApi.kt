package com.example.practice.data.api

import com.example.practice.data.dto.StudioPageDto
import com.example.practice.data.dto.StudioDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StudiosApi {
    // https://api.kinopoisk.dev/v1.4/studio?page=1&limit=50
    @GET("v1.4/studio")
    suspend fun getStudiosPage(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<StudioPageDto>
    
    // https://api.kinopoisk.dev/v1.4/studio?id=1
    @GET("v1.4/studio")
    suspend fun getStudioById(
        @Query("id") id: String
    ): Response<StudioPageDto>
}
