package com.example.courses.core.domain.model

data class Course(
    val id: Int,
    val title: String,
    val text: String,
    val price: String,
    val rate: Float,
    val startDate: String,
    val publishDate: String, // Строка в формате ГГГГ-ММ-ДД для сортировки
    val hasLike: Boolean
)
