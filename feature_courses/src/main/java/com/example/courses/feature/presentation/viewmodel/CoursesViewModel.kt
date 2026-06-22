package com.example.courses.feature.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courses.core.domain.model.Course
import com.example.courses.core.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CoursesViewModel(
    private val repository: CoursesRepository
) : ViewModel() {

    // Вьюмодель теперь просто транслирует чистый поток курсов из репозитория
    val mainScreenState: StateFlow<List<Course>> = repository.getCourses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    suspend fun toggleLike(courseId: Int) {
        repository.toggleLike(courseId)
    }
}
