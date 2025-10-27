package com.example.practice.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practice.data.database.dao.FavoriteStudioDao
import com.example.practice.data.database.entity.FavoriteStudio

@Database(
    entities = [FavoriteStudio::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteStudioDao(): FavoriteStudioDao
}
