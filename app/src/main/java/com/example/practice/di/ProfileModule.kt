package com.example.practice.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.practice.data.repository.ProfileRepositoryImpl
import com.example.practice.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

private val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore("profile_prefs")

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileBindModule {
    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ProfileProvideModule {
    @Provides
    @Singleton
    @Named("profile_prefs")
    fun provideProfileDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.profileDataStore
}



