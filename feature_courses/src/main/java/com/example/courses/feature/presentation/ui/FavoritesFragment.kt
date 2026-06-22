package com.example.courses.feature.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.courses.feature.R
import com.example.courses.feature.presentation.adapter.CoursesAdapter
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: CoursesViewModel by viewModel()
    
    private val coursesAdapter = CoursesAdapter(
        onDetailsClick = { course ->
            openCourseDetails(course.id)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMain)
        recyclerView.adapter = coursesAdapter

        // Фильтруем чистый список курсов по флагу из базы Room
        viewModel.mainScreenState
            .onEach { courses ->
                val favoriteCourses = courses.filter { it.isLiked }
                coursesAdapter.submitList(favoriteCourses)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun openCourseDetails(courseId: Int) {
        val fragment = CourseDetailFragment.newInstance(courseId)
        val context = requireContext()
        val containerId = context.resources.getIdentifier("fragment_container", "id", context.packageName)
        if (containerId != 0) {
            parentFragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
