package com.example.courses.core.domain.model

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    val rating: Float,
    val startDate: String,
    val isLiked: Boolean
)
