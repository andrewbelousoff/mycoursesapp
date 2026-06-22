package com.example.courses.feature.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.courses.feature.R
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseDetailFragment : Fragment(R.layout.fragment_course_detail) {

    private val viewModel: CoursesViewModel by viewModel()
    private var courseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getInt(ARG_COURSE_ID, -1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val tvTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvDetailDescription)
        val btnToggleLike = view.findViewById<Button>(R.id.btnToggleLike)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (courseId != -1) {
            btnToggleLike.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.toggleLike(courseId)
                }
            }

            viewModel.mainScreenState
                .onEach { courses ->
                    val course = courses.firstOrNull { it.id == courseId }
                    
                    course?.let {
                        tvTitle.text = it.title
                        tvDescription.text = it.description
                        
                        if (it.isLiked) {
                            btnToggleLike.text = "Удалить из избранного"
                            btnToggleLike.setBackgroundColor(android.graphics.Color.parseColor("#D32F2F"))
                        } else {
                            btnToggleLike.text = "В избранное"
                            btnToggleLike.setBackgroundColor(android.graphics.Color.parseColor("#00C853"))
                        }
                    }
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    companion object {
        private const val ARG_COURSE_ID = "course_id"

        fun newInstance(courseId: Int) = CourseDetailFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_COURSE_ID, courseId)
            }
        }
    }
}
