package com.example.courses.feature.presentation.adapter

import com.example.courses.core.domain.model.Course
import com.example.courses.feature.databinding.ItemCourseBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun courseAdapterDelegate(
    onCourseClick: (Course) -> Unit,
    onLikeClick: (Course) -> Unit
) = adapterDelegateViewBinding<Course, Course, ItemCourseBinding>(
    { layoutInflater, parent -> ItemCourseBinding.inflate(layoutInflater, parent, false) }
) {
    binding.root.setOnClickListener {
        onCourseClick(item)
    }

    binding.btnLike.setOnClickListener {
        onLikeClick(item)
    }

    bind {
        binding.tvTitle.text = item.title
        binding.tvDescription.text = item.description
        binding.tvPrice.text = "${item.price} Р"
        binding.tvRating.text = "★ ${item.rating}"
        
        // В реальном проекте здесь будут иконки сердечек из ресурсов
        if (item.isLiked) {
            binding.btnLike.alpha = 1.0f
        } else {
            binding.btnLike.alpha = 0.3f
        }
    }
}
