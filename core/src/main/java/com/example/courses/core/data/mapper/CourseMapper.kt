package com.example.courses.core.data.mapper

import com.example.courses.core.data.model.CourseDto
import com.example.courses.core.domain.model.Course

fun CourseDto.toDomain(): Course {
    return Course(
        id = this.id ?: 0,
        title = this.title.orEmpty(),
        description = this.text.orEmpty(),
        price = this.price.orEmpty(),
        rating = this.rate?.toFloatOrNull() ?: 0.0f,
        startDate = this.startDate.orEmpty(),
        isLiked = this.hasLike ?: false
    )
}
