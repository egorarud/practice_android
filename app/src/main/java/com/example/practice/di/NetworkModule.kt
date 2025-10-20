package com.example.practice.di

import com.example.practice.data.api.StudiosApi
import com.example.practice.data.repository.NetworkStudiosRepository
import com.example.practice.domain.repository.StudiosRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    
    @Binds
    abstract fun bindStudiosRepository(
        networkStudiosRepository: NetworkStudiosRepository
    ): StudiosRepository
    
    companion object {
        
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val apiKeyInterceptor = Interceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("X-API-KEY", "7VCSYW7-MR44YYR-PSJZ74T-616QAV4")
                    .build()
                chain.proceed(request)
            }
            
            return OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }
        
        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.kinopoisk.dev/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        
        @Provides
        @Singleton
        fun provideStudiosApi(retrofit: Retrofit): StudiosApi {
            return retrofit.create(StudiosApi::class.java)
        }
    }
}
