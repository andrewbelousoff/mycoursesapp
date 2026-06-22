package com.example.courses.feature.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.courses.feature.R
import com.example.courses.feature.presentation.adapter.CoursesAdapter
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavoritesFragment : Fragment(R.layout.fragment_main) {

    // ИСПРАВЛЕНО: Смотрим в ту же самую Вьюмодель, что и Главный экран
    private val viewModel: CoursesViewModel by activityViewModel()
    private var coursesAdapter: CoursesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CoursesAdapter(
            onLikeClick = { course ->
                viewLifecycleOwner.lifecycleScope.launch { viewModel.toggleLike(course.id) }
            },
            onDetailsClick = { course -> openCourseDetails(course.id) }
        )
        coursesAdapter = adapter

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMain)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        view.findViewById<View>(R.id.topBar)?.visibility = View.GONE
        view.findViewById<View>(R.id.tvSortButton)?.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { favoriteCourses ->
                    adapter.submitList(favoriteCourses)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        coursesAdapter = null
    }

    private fun openCourseDetails(courseId: Int) {
        val fragment = CourseDetailFragment.newInstance(courseId)
        val containerId = requireContext().resources.getIdentifier("fragment_container", "id", "com.example.courses")
        if (containerId != 0) {
            parentFragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
