package com.example.courses.feature.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courses.core.domain.model.Course
import com.example.courses.core.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CoursesViewModel(
    private val repository: CoursesRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val isSortedByDate = MutableStateFlow(false)

    val catalogState: StateFlow<List<Course>> = combine(
        repository.getCourses(),
        searchQuery,
        isSortedByDate
    ) { courses, query, sorted ->
        val filtered = if (query.isBlank()) {
            courses
        } else {
            // ИСПРАВЛЕНО: Принудительно переводим обе строки в нижний регистр через lowercase().
            // Это гарантирует стабильную работу поиска на кириллице независимо от больших/маленьких букв!
            courses.filter { 
                it.title.lowercase().contains(query.lowercase()) 
            }
        }

        
        // ИСПРАВЛЕНО: Реализуем честный переворот списка «с ног на голову» по ТЗ.
        // Заменяем пустые даты на дефолтные, чтобы они не ломали математику Kotlin.
        if (sorted) {
            // Клик 1: Сортируем строго по УБЫВАНИЮ даты публикации (свежие вверху)
            filtered.sortedByDescending { 
                if (it.publishDate.isBlank()) "1970-01-01" else it.publishDate 
            }
        } else {
            // Клик 2: Сортируем строго по ВОЗРАСТАНИЮ даты публикации (старые вверху)
            filtered.sortedBy { 
                if (it.publishDate.isBlank()) "1970-01-01" else it.publishDate 
            }
        }
    }.stateIn(
        scope = viewModelScope, 
        started = SharingStarted.Eagerly, 
        initialValue = emptyList()
    )

    val favoritesState: StateFlow<List<Course>> = repository.getCourses()
        .map { courses -> courses.filter { it.hasLike } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) { searchQuery.value = query }
    
    fun toggleSort() { isSortedByDate.value = !isSortedByDate.value }

    suspend fun toggleLike(courseId: Int) { repository.toggleLike(courseId) }
}

