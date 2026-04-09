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
import com.example.studyplanner.databinding.FragmentAddStudyBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddStudyFragment : Fragment() {

    private var _binding: FragmentAddStudyBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: StudyDatabase
    private val selectedCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = StudyDatabase.getDatabase(requireContext())

        binding.buttonPickDate.setOnClickListener {
            showDatePicker()
        }

        binding.buttonPickTime.setOnClickListener {
            showTimePicker()
        }

        binding.buttonSave.setOnClickListener {
            saveTask()
        }

        updateDateTimeText()
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
        binding.textViewSelectedDateTime.text = formatter.format(selectedCalendar.time)
    }

    private fun saveTask() {
        val title = binding.editTextTitle.text.toString().trim()
        val category = binding.editTextCategory.text.toString().trim()
        val location = binding.editTextLocation.text.toString().trim()
        val dateTime = selectedCalendar.timeInMillis

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (dateTime < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Past date is not allowed", Toast.LENGTH_SHORT).show()
            return
        }

        val task = StudyTask(
            title = title,
            category = category,
            location = location,
            dateTime = dateTime
        )

        lifecycleScope.launch {
            database.studyTaskDao().insertTask(task)
            Toast.makeText(requireContext(), "Task saved", Toast.LENGTH_SHORT).show()

            binding.editTextTitle.text?.clear()
            binding.editTextCategory.text?.clear()
            binding.editTextLocation.text?.clear()

            selectedCalendar.timeInMillis = System.currentTimeMillis()
            updateDateTimeText()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}