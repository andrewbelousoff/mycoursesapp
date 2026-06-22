package com.example.courses.core.data.mapper

import com.example.courses.core.data.repository.LikedCourseEntity
import com.example.courses.core.domain.model.Course

fun Course.toEntity(): LikedCourseEntity {
    return LikedCourseEntity(
        courseId = this.id
    )
}

// ИСПРАВЛЕНО: Маппинг полей строго под новые требования твоего ТЗ
fun Course.mapToDomain(isLiked: Boolean): Course {
    return Course(
        id = this.id,
        title = this.title,
        text = this.text,         // Новое поле text
        price = this.price,
        rate = this.rate,         // Новое поле rate
        startDate = this.startDate,
        publishDate = this.publishDate, // Поле даты для сортировки
        hasLike = isLiked         // Новое поле hasLike
    )
}
