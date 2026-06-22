package com.example.courses.feature.data.repository

import android.content.Context
import com.example.courses.core.domain.model.Course
import com.example.courses.core.domain.repository.CoursesRepository
import com.example.courses.core.data.repository.CoursesDatabase
import com.example.courses.core.data.repository.LikedCourseEntity
import com.example.courses.feature.data.model.CoursesResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class CoursesRepositoryImpl(
    private val context: Context,
    private val gson: Gson = Gson()
) : CoursesRepository {

    private val likedCoursesDao = CoursesDatabase.getDatabase(context.applicationContext).likedCoursesDao()
    private val _coursesState = MutableStateFlow<List<Course>>(emptyList())
    private var isInitialized = false

    override fun getCourses(): Flow<List<Course>> {
        if (!isInitialized) {
            isInitialized = true
            GlobalScope.launch(Dispatchers.IO) {
                val coursesFromAsset = loadCoursesFromAssets()
                val updatedCourses = coursesFromAsset.map { course ->
                    course.copy(isLiked = likedCoursesDao.isLiked(course.id))
                }
                _coursesState.value = updatedCourses
            }
        }
        return _coursesState.asStateFlow()
    }

    private fun loadCoursesFromAssets(): List<Course> {
        return try {
            val inputStream = context.assets.open("courses.json")
            val reader = InputStreamReader(inputStream)
            
            // Автоматический парсинг Gson без риска вылетов по типам данных
            val response = gson.fromJson(reader, CoursesResponse::class.java)
            
            // Мапим (конвертируем) DTO-модель в твою оригинальную доменную модель Course.kt
            response?.courses?.map { dto ->
                Course(
                    id = dto.id.toInt(),              // Безопасно переводим строку "1" в число 1
                    title = dto.title,
                    description = dto.description,
                    price = dto.price.toString(),     // Безопасно переводим число 4500 в строку "4500"
                    rating = dto.rating,
                    startDate = dto.startDate,
                    isLiked = false
                )
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getFavoriteCourses(): Flow<List<Course>> {
        return _coursesState.asStateFlow().map { list ->
            list.filter { it.isLiked }
        }
    }

    override fun getCourseById(id: Int): Flow<Course?> {
        return _coursesState.asStateFlow().map { list ->
            list.find { it.id == id }
        }
    }

    override suspend fun toggleLike(courseId: Int) {
        withContext(Dispatchers.IO) {
            val isAlreadyLiked = likedCoursesDao.isLiked(courseId)

            if (isAlreadyLiked) {
                likedCoursesDao.deleteLike(LikedCourseEntity(courseId))
            } else {
                likedCoursesDao.insertLike(LikedCourseEntity(courseId))
            }

            _coursesState.update { currentList ->
                currentList.map {
                    if (it.id == courseId) it.copy(isLiked = !it.isLiked) else it
                }
            }
        }
    }
}
