package com.example.courses.feature.data.model

// Корневой объект {"courses": [...]}
data class CoursesResponse(
    val courses: List<NetworkCourseDto>
)

// Сетевая модель один в один по твоему файлу из интернета
data class NetworkCourseDto(
    val id: Int,
    val title: String,
    val text: String,
    val price: String,
    val rate: String,       // В JSON строка "4.9" в кавычках — принимаем как String!
    val startDate: String,
    val hasLike: Boolean,
    val publishDate: String
)
