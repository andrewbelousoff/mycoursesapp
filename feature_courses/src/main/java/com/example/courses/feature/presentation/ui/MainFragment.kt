package com.example.courses.feature.presentation.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.courses.feature.R
import com.example.courses.feature.presentation.adapter.CoursesAdapter
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MainFragment : Fragment(R.layout.fragment_main) {

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

        val etSearch = view.findViewById<EditText>(R.id.et_catalog_search)
        val tvSortButton = view.findViewById<TextView>(R.id.tvSortButton)

        etSearch?.filters = arrayOf()
        
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { 
                viewModel.setSearchQuery(s?.toString() ?: "") 
            }
        })

        // ИСПРАВЛЕНО: Скролл наверх триггерится СТРОГО при клике на кнопку сортировки,
        // давая адаптеру 50мс на перестроение. При кликах на звезды экран больше не прыгнет!
        tvSortButton?.setOnClickListener { 
            viewModel.toggleSort() 
            viewLifecycleOwner.lifecycleScope.launch {
                delay(50)
                recyclerView.scrollToPosition(0)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.catalogState.collect { courses ->
                    adapter.submitList(courses)
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

