package com.example.courses.feature.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.courses.feature.R
import com.example.courses.core.domain.model.Course

class CoursesAdapter(
    private val onDetailsClick: (Course) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    private var items: List<Course> = emptyList()

    fun submitList(newList: List<Course>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = items[position]
        holder.tvTitle.text = course.title
        holder.tvPrice.text = "${course.price} ₽"
        holder.btnDetails.setOnClickListener { onDetailsClick(course) }
    }

    override fun getItemCount(): Int = items.size

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvCourseTitle)
        val tvPrice: TextView = view.findViewById(R.id.tvCoursePrice)
        val btnDetails: Button = view.findViewById(R.id.btn_details)
    }
}
