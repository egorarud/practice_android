package com.example.practice.data.repository

import com.example.practice.data.api.StudiosApi
import com.example.practice.data.dto.StudioPageDto
import com.example.practice.data.dto.StudioDto
import com.example.practice.data.mapper.toDomain
import com.example.practice.domain.model.Studio
import com.example.practice.domain.model.StudioPage
import com.example.practice.domain.repository.StudiosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStudiosRepository @Inject constructor(
    private val api: StudiosApi
) : StudiosRepository {
    
    override suspend fun getStudiosPage(page: Int, limit: Int): Result<StudioPage> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getStudiosPage(page, limit)
                if (response.isSuccessful) {
                    val studioPageDto: StudioPageDto? = response.body()
                    if (studioPageDto != null) {
                        Result.success(studioPageDto.toDomain())
                    } else {
                        Result.failure(Exception("Сервер вернул пустой ответ. Попробуйте обновить данные."))
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Некорректный запрос. Проверьте параметры."
                        401 -> "Ошибка авторизации. Проверьте API ключ."
                        403 -> "Доступ запрещен. Превышен лимит запросов."
                        404 -> "Данные не найдены."
                        429 -> "Превышен лимит запросов. Попробуйте позже."
                        500 -> "Внутренняя ошибка сервера. Попробуйте позже."
                        502, 503, 504 -> "Сервер временно недоступен. Попробуйте позже."
                        else -> "Ошибка сервера (${response.code()}). Попробуйте позже."
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("Нет подключения к интернету. Проверьте соединение."))
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("Превышено время ожидания. Проверьте соединение."))
            } catch (e: java.net.ConnectException) {
                Result.failure(Exception("Не удается подключиться к серверу. Проверьте соединение."))
            } catch (e: Exception) {
                Result.failure(Exception("Неожиданная ошибка: ${e.message ?: "Неизвестная ошибка"}"))
            }
        }
    }
    
    override suspend fun getStudioById(id: String): Result<Studio?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getStudioById(id)
                if (response.isSuccessful) {
                    val studioPageDto: StudioPageDto? = response.body()
                    if (studioPageDto != null && studioPageDto.docs.isNotEmpty()) {
                        // Берем первую студию из списка
                        Result.success(studioPageDto.docs.first().toDomain())
                    } else {
                        Result.failure(Exception("Студия с ID '$id' не найдена."))
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Некорректный запрос. Проверьте ID студии."
                        401 -> "Ошибка авторизации. Проверьте API ключ."
                        403 -> "Доступ запрещен. Превышен лимит запросов."
                        404 -> "Студия с ID '$id' не найдена."
                        429 -> "Превышен лимит запросов. Попробуйте позже."
                        500 -> "Внутренняя ошибка сервера. Попробуйте позже."
                        502, 503, 504 -> "Сервер временно недоступен. Попробуйте позже."
                        else -> "Ошибка сервера (${response.code()}). Попробуйте позже."
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("Нет подключения к интернету. Проверьте соединение."))
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("Превышено время ожидания. Проверьте соединение."))
            } catch (e: java.net.ConnectException) {
                Result.failure(Exception("Не удается подключиться к серверу. Проверьте соединение."))
            } catch (e: Exception) {
                Result.failure(Exception("Неожиданная ошибка: ${e.message ?: "Неизвестная ошибка"}"))
            }
        }
    }
}
