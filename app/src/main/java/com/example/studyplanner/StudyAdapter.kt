package com.example.studyplanner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studyplanner.databinding.ItemStudyBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudyAdapter(
    private val taskList: List<StudyTask>,
    private val onItemClick: (StudyTask) -> Unit
) : RecyclerView.Adapter<StudyAdapter.StudyViewHolder>() {

    inner class StudyViewHolder(val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val binding = ItemStudyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.textViewTitle.text = task.title
        holder.binding.textViewCategory.text = task.category
        holder.binding.textViewLocation.text = task.location
        holder.binding.textViewDate.text = formatDate(task.dateTime)

        holder.itemView.setOnClickListener {
            onItemClick(task)
        }
    }

    override fun getItemCount(): Int = taskList.size

    private fun formatDate(dateTime: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return formatter.format(Date(dateTime))
    }
}