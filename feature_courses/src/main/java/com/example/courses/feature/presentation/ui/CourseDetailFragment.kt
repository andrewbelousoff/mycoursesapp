package com.example.courses.feature.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.courses.feature.R
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CourseDetailFragment : Fragment(R.layout.fragment_course_detail) {

    // ИСПРАВЛЕНО: Детали тоже делят общий стейт лайков
    private val viewModel: CoursesViewModel by activityViewModel()
    private var courseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { courseId = it.getInt(ARG_COURSE_ID, -1) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val tvTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvDetailDescription)
        val btnToggleLike = view.findViewById<Button>(R.id.btnToggleLike)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        if (courseId != -1) {
            btnToggleLike.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch { viewModel.toggleLike(courseId) }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.catalogState.collectLatest { courses ->
                        val course = courses.firstOrNull { it.id == courseId }
                        course?.let {
                            tvTitle.text = it.title
                            tvDescription.text = it.text
                            
                            if (it.hasLike) {
                                btnToggleLike.text = "Удалить из избранного"
                                btnToggleLike.setBackgroundColor("#D32F2F".toColorInt())
                            } else {
                                btnToggleLike.text = "В избранное"
                                btnToggleLike.setBackgroundColor("#00C853".toColorInt())
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_COURSE_ID = "course_id"
        fun newInstance(courseId: Int) = CourseDetailFragment().apply {
            arguments = Bundle().apply { putInt(ARG_COURSE_ID, courseId) }
        }
    }
}
