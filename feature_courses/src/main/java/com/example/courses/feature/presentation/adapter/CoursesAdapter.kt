package com.example.courses.feature.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.courses.feature.R
import com.example.courses.core.domain.model.Course

class CoursesAdapter(
    private val onLikeClick: (Course) -> Unit,
    private val onDetailsClick: (Course) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(newList: List<Course>) {
        differ.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = differ.currentList[position]
        
        holder.tvTitle.text = course.title
        holder.tvDescription.text = course.text
        holder.tvPrice.text = "${course.price} ₽"
        
        // ИСПРАВЛЕНО: DiffUtil сам контролирует кэш, теперь звезды не будут глючить и залипать
        if (course.hasLike) {
            holder.btnLikeWidget.setImageResource(android.R.drawable.btn_star_big_on)
            holder.btnLikeWidget.setBackgroundColor(android.graphics.Color.parseColor("#00C853"))
        } else {
            holder.btnLikeWidget.setImageResource(android.R.drawable.btn_star_big_off)
            holder.btnLikeWidget.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        holder.btnLikeWidget.setOnClickListener { onLikeClick(course) }
        holder.btnDetails.setOnClickListener { onDetailsClick(course) }
    }

    override fun getItemCount(): Int = differ.currentList.size

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvCourseTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvCourseDescription)
        val tvPrice: TextView = view.findViewById(R.id.tvCoursePrice)
        val btnLikeWidget: ImageButton = view.findViewById(R.id.btnLikeWidget)
        val btnDetails: Button = view.findViewById(R.id.btn_details)
    }
}

