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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: CoursesViewModel by viewModel()
    
    // Переменная для удержания ссылки на текущий живой адаптер
    private var coursesAdapter: CoursesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ИСПРАВЛЕНО: Создаем адаптер строго внутри onViewCreated. Он привязан к живой верстке!
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

        val etSearch = view.findViewById<EditText>(R.id.et_search)
        val tvSortButton = view.findViewById<TextView>(R.id.tvSortButton)

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { 
                viewModel.setSearchQuery(s?.toString() ?: "") 
            }
        })

        tvSortButton?.setOnClickListener { viewModel.toggleSort() }
        
        // 1. Слушаем состояние сортировки и динамически меняем стрелочку на кнопке
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isSortedByDate.collectLatest { isSorted ->
                    if (isSorted) {
                        tvSortButton?.text = "По дате добавления ↓"
                    } else {
                        tvSortButton?.text = "По дате добавления ↑"
                    }
                }
            }
        }

        // 2. Слушаем актуальный поток данных каталога
                // ИСПРАВЛЕНО: Используем collect вместо collectLatest, чтобы корутины отрисовки 
        // выполнялись строго последовательно и не отменяли друг друга при быстрых кликах!
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
        coursesAdapter = null // Зачищаем ссылку, защищая приложение от утечек памяти
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

