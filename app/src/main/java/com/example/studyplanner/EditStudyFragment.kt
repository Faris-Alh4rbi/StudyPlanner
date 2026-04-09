package com.example.studyplanner

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.studyplanner.databinding.FragmentEditStudyBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditStudyFragment : Fragment() {

    private var _binding: FragmentEditStudyBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: StudyDatabase
    private var taskId: Int = -1
    private var currentTask: StudyTask? = null
    private val selectedCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskId = arguments?.getInt("taskId", -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = StudyDatabase.getDatabase(requireContext())

        binding.buttonPickEditDate.setOnClickListener {
            showDatePicker()
        }

        binding.buttonPickEditTime.setOnClickListener {
            showTimePicker()
        }

        binding.buttonUpdate.setOnClickListener {
            updateTask()
        }

        binding.buttonDelete.setOnClickListener {
            deleteTask()
        }

        loadTask()
    }

    private fun loadTask() {
        lifecycleScope.launch {
            val task = database.studyTaskDao().getTaskById(taskId)

            if (task == null) {
                Toast.makeText(requireContext(), "Task not found", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
                return@launch
            }

            currentTask = task
            selectedCalendar.timeInMillis = task.dateTime

            binding.editTextEditTitle.setText(task.title)
            binding.editTextEditCategory.setText(task.category)
            binding.editTextEditLocation.setText(task.location)
            updateDateTimeText()
        }
    }

    private fun showDatePicker() {
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            selectedCalendar.set(Calendar.YEAR, selectedYear)
            selectedCalendar.set(Calendar.MONTH, selectedMonth)
            selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            updateDateTimeText()
        }, year, month, day).show()
    }

    private fun showTimePicker() {
        val hour = selectedCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = selectedCalendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedCalendar.set(Calendar.MINUTE, selectedMinute)
            selectedCalendar.set(Calendar.SECOND, 0)
            selectedCalendar.set(Calendar.MILLISECOND, 0)
            updateDateTimeText()
        }, hour, minute, false).show()
    }

    private fun updateDateTimeText() {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        binding.textViewEditSelectedDateTime.text = formatter.format(selectedCalendar.time)
    }

    private fun updateTask() {
        val oldTask = currentTask ?: return

        val title = binding.editTextEditTitle.text.toString().trim()
        val category = binding.editTextEditCategory.text.toString().trim()
        val location = binding.editTextEditLocation.text.toString().trim()
        val dateTime = selectedCalendar.timeInMillis

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (dateTime < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Past date is not allowed", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTask = oldTask.copy(
            title = title,
            category = category,
            location = location,
            dateTime = dateTime
        )

        lifecycleScope.launch {
            database.studyTaskDao().updateTask(updatedTask)
            Toast.makeText(requireContext(), "Task updated", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun deleteTask() {
        val oldTask = currentTask ?: return

        lifecycleScope.launch {
            database.studyTaskDao().deleteTask(oldTask)
            Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}