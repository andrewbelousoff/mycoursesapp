package com.example.courses.feature.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.courses.feature.R

class CourseDetailFragment : Fragment(R.layout.fragment_course_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val courseInfo = arguments?.getString(ARG_COURSE_INFO) ?: "Данные отсутствуют"
        
        val tvTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        tvTitle.text = courseInfo

        // Находим нашу новую кнопку Назад
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            // Возвращаемся на предыдущий фрагмент (к списку курсов)
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val ARG_COURSE_INFO = "arg_course_info"

        fun newInstance(info: String): CourseDetailFragment {
            return CourseDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_INFO, info)
                }
            }
        }
    }
}
