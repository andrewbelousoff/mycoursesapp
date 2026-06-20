package com.example.courses.feature.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.courses.core.data.repository.CoursesRepositoryImpl
import com.example.courses.core.domain.model.Course
import com.example.courses.feature.R
import com.example.courses.feature.databinding.FragmentMainBinding
import com.example.courses.feature.presentation.adapter.courseAdapterDelegate
import com.example.courses.feature.presentation.viewmodel.CoursesUiState
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    // Инициализируем репозиторий и ViewModel напрямую для экономии времени (без DI-библиотек)
    private val viewModel: CoursesViewModel by lazy {
        CoursesViewModel(CoursesRepositoryImpl(requireContext().applicationContext))
    }

    private var allCourses: List<Course> = emptyList()

    private val adapter by lazy {
        AsyncListDifferDelegationAdapter(
            object : DiffUtil.ItemCallback<Course>() {
                override fun itemsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem.id == newItem.id
                override fun contentsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem == newItem
            },
            courseAdapterDelegate(
                onCourseClick = { course ->
                    Toast.makeText(requireContext(), "Клик на курс: ${course.title}", Toast.LENGTH_SHORT).show()
                },
                onLikeClick = { course ->
                    viewModel.onLikeClicked(course)
                }
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        binding.rvCourses.adapter = adapter
        setupSearchView()

        viewModel.uiState
            .onEach { state -> renderState(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
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
            CoursesUiState.Loading -> { /* Идет загрузка */ }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterCourses(newText.orEmpty())
                return true
            }
        })
    }

    private fun filterCourses(query: String) {
        if (query.isEmpty()) {
            adapter.items = allCourses
        } else {
            adapter.items = allCourses.filter {
                it.title.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
