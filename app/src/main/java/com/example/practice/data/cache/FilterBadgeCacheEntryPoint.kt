package com.example.practice.data.cache

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Entry point для получения FilterBadgeCache из DI в Compose функциях
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface FilterBadgeCacheEntryPoint {
    fun filterBadgeCache(): FilterBadgeCache
}

