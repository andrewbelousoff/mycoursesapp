package com.example.courses.core.domain.repository

import com.example.courses.core.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CoursesRepository {
    fun getCourses(): Flow<List<Course>>
    suspend fun toggleLike(courseId: Int)
}
