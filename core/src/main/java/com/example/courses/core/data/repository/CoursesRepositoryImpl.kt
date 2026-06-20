package com.example.courses.core.data.repository

import android.content.Context
import com.example.courses.core.data.mapper.toDomain
import com.example.courses.core.data.model.CoursesResponseDto
import com.example.courses.core.domain.model.Course
import com.example.courses.core.domain.repository.CoursesRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.InputStreamReader

class CoursesRepositoryImpl(
    private val context: Context,
    private val gson: Gson = Gson()
) : CoursesRepository {

    private val _coursesState = MutableStateFlow<List<Course>>(emptyList())
    private var isInitialized = false

    override fun getCourses(): Flow<List<Course>> {
        if (!isInitialized) {
            loadCoursesFromAssets()
        }
        return _coursesState.asStateFlow()
    }

    override suspend fun toggleLike(courseId: Int) {
        _coursesState.update { currentList ->
            currentList.map {
                if (it.id == courseId) it.copy(isLiked = !it.isLiked) else it
            }
        }
    }

    private fun loadCoursesFromAssets() {
        try {
            context.assets.open("courses.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val response = gson.fromJson(reader, CoursesResponseDto::class.java)
                    val domainCourses = response.courses.map { it.toDomain() }
                    _coursesState.value = domainCourses
                    isInitialized = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
