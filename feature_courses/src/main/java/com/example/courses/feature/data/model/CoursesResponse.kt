package com.example.courses.feature.data.model

import com.example.courses.core.domain.model.Course

// Обертка под корневой объект {"courses": [...]}
data class CoursesResponse(
    val courses: List<CourseDto>
)

// Модель, которая идеально принимает кавычки и числа из твоего файла courses.json
data class CourseDto(
    val id: String,          // В JSON строка "1"
    val title: String,
    val description: String,
    val price: Int,          // В JSON число 4500
    val rating: Float,
    val startDate: String
)
