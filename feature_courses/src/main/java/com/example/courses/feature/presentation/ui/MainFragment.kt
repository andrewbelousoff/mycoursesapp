package com.example.courses.feature.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.example.courses.feature.R
import com.example.courses.feature.data.repository.CoursesRepositoryImpl
import com.example.courses.feature.databinding.FragmentMainBinding
import com.example.courses.core.domain.model.Course
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import com.example.courses.feature.presentation.viewmodel.CoursesUiState
import com.example.courses.feature.presentation.adapter.courseAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CoursesViewModel by lazy {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = CoursesRepositoryImpl(requireActivity().applicationContext)
                return CoursesViewModel(repository) as T
            }
        }
        ViewModelProvider(this, factory)[CoursesViewModel::class.java]
    }

    private var allCourses: List<Course> = emptyList()

    private val coursesAdapter by lazy {
        AsyncListDifferDelegationAdapter<Course>(
            object : DiffUtil.ItemCallback<Course>() {
                override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem.id == newItem.id
                override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem == newItem
            },
            courseAdapterDelegate(
                onCourseClick = { course ->
                    val infoText = "${course.title}\n\n${course.description}\n\nЦена: ${course.price} rgb."
                    val detailFragment = CourseDetailFragment.newInstance(infoText)
                    val containerId = requireActivity().resources.getIdentifier("fragment_container", "id", requireActivity().packageName)
                    if (containerId != 0) {
                        parentFragmentManager.beginTransaction().replace(containerId, detailFragment).addToBackStack(null).commit()
                    }
                },
                onLikeClick = { course ->
                    // АДАПТЕР ФОРМАТА: переводим строковый id объекта в число Int строго для вызова ViewModel
                    val adaptedIdInt = course.id.toString().toIntOrNull() ?: 0
                    viewModel.onLikeClicked(course) 
                }
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        binding.rvCourses.adapter = coursesAdapter
        setupSearchView()

        viewModel.uiState
            .onEach { state -> renderState(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterCourses(newText ?: "")
                return true
            }
        })
    }

    private fun filterCourses(query: String) {
        val filteredList = if (query.isEmpty()) {
            allCourses
        } else {
            allCourses.filter { course ->
                course.title.contains(query, ignoreCase = true) ||
                course.description.contains(query, ignoreCase = true)
            }
        }
        coursesAdapter.items = filteredList
        coursesAdapter.notifyDataSetChanged()
    }

    private fun renderState(state: CoursesUiState) {
        binding.progressBar.isVisible = state is CoursesUiState.Loading
        when (state) {
            is CoursesUiState.Success -> {
                allCourses = state.courses
                filterCourses(binding.searchView.query.toString())
            }
            is CoursesUiState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            }
            CoursesUiState.Loading -> { /* Загрузка */ }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
