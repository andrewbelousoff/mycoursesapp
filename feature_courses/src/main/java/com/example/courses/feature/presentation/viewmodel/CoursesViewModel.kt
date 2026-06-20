package com.example.courses.feature.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courses.core.domain.model.Course
import com.example.courses.core.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface CoursesUiState {
    object Loading : CoursesUiState
    data class Success(val courses: List<Course>) : CoursesUiState
    data class Error(val message: String) : CoursesUiState
}

class CoursesViewModel(
    private val repository: CoursesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CoursesUiState>(CoursesUiState.Loading)
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            repository.getCourses()
                .catch { e ->
                    _uiState.value = CoursesUiState.Error(e.localizedMessage ?: "Unknown Error")
                }
                .collect { coursesList ->
                    _uiState.value = CoursesUiState.Success(coursesList)
                }
        }
    }

    fun onLikeClicked(course: Course) {
        viewModelScope.launch {
            repository.toggleLike(course.id)
        }
    }
}
