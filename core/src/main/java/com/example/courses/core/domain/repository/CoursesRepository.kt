package com.example.courses.core.domain.repository

import com.example.courses.core.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CoursesRepository {
    fun getCourses(): Flow<List<Course>>
    
    // ДОБАВЛЕНО: Сигнатура для экрана Избранного
    fun getFavoriteCourses(): Flow<List<Course>>
    
    // ДОБАВЛЕНО: Сигнатура для экрана Деталей (используем Int для ID)
    fun getCourseById(id: Int): Flow<Course?>
    
    suspend fun toggleLike(courseId: Int)
}
