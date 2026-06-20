package com.example.courses.core.data.model

import com.google.gson.annotations.SerializedName

data class CoursesResponseDto(
    @SerializedName("courses") val courses: List<CourseDto>
)

data class CourseDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("price") val price: String?,
    @SerializedName("rate") val rate: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("hasLike") val hasLike: Boolean?,
    @SerializedName("publishDate") val publishDate: String?
)
