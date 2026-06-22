package com.example.courses.feature.data.repository

import android.content.Context
import com.example.courses.core.domain.model.Course
import com.example.courses.core.domain.repository.CoursesRepository
import com.example.courses.core.data.repository.CoursesDatabase
import com.example.courses.core.data.repository.LikedCourseEntity
import com.example.courses.feature.data.model.CoursesResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private val likedCoursesDao by lazy { 
        CoursesDatabase.getDatabase(context.applicationContext).likedCoursesDao() 
    }
    
    // ЕДИНСТВЕННЫЙ ВЕЧНЫЙ ИСТОЧНИК ПРАВДЫ В ПАМЯТИ ПРИЛОЖЕНИЯ
    private val _coursesState = MutableStateFlow<List<Course>>(emptyList())
    private var isInitialized = false

    override fun getCourses(): Flow<List<Course>> {
        if (!isInitialized) {
            isInitialized = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inputStream = context.assets.open("courses.json")
                    val reader = InputStreamReader(inputStream)
                    val response = gson.fromJson(reader, CoursesResponse::class.java)
                    
                    val networkCourses = response?.courses?.map { dto ->
                        Course(
                            id = dto.id,
                            title = dto.title,
                            text = dto.text,
                            price = dto.price,
                            rate = dto.rate.toFloatOrNull() ?: 0.0f,
                            startDate = dto.startDate,
                            publishDate = dto.publishDate,
                            hasLike = false
                        )
                    } ?: emptyList()

                    val updatedCourses = networkCourses.map { course ->
                        course.copy(hasLike = likedCoursesDao.isLiked(course.id))
                    }
                    _coursesState.value = updatedCourses
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return _coursesState.asStateFlow()
    }

    override fun getFavoriteCourses(): Flow<List<Course>> {
        return _coursesState.asStateFlow().map { list -> list.filter { it.hasLike } }
    }

    override fun getCourseById(id: Int): Flow<Course?> {
        return _coursesState.asStateFlow().map { list -> list.find { it.id == id } }
    }

    override suspend fun toggleLike(courseId: Int) {
        _coursesState.update { currentList ->
            currentList.map {
                if (it.id == courseId) it.copy(hasLike = !it.hasLike) else it
            }
        }

        withContext(Dispatchers.IO) {
            try {
                val isAlreadyLiked = likedCoursesDao.isLiked(courseId)
                if (isAlreadyLiked) {
                    likedCoursesDao.deleteLike(LikedCourseEntity(courseId = courseId))
                } else {
                    likedCoursesDao.insertLike(LikedCourseEntity(courseId = courseId))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

