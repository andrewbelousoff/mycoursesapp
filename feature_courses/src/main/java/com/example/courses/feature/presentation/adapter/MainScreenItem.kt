package com.example.courses.feature.presentation.adapter

import com.example.courses.core.domain.model.Course

sealed interface MainScreenItem {
    object SearchBar : MainScreenItem
    data class CourseItem(val course: Course) : MainScreenItem
}
