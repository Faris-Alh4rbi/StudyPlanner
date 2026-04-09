package com.example.studyplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyplanner.databinding.FragmentStudyListBinding
import kotlinx.coroutines.launch

class StudyListFragment : Fragment() {

    private var _binding: FragmentStudyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: StudyDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = StudyDatabase.getDatabase(requireContext())
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val taskList = database.studyTaskDao().getAllTasks()

            binding.recyclerViewTasks.adapter = StudyAdapter(taskList) { selectedTask ->
                findNavController().navigate(
                    R.id.action_studyListFragment_to_editStudyFragment,
                    bundleOf("taskId" to selectedTask.id)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}