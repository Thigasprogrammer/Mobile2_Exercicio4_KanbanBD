package com.thiagosilva.navegabilidadeentreastelas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.thiagosilva.navegabilidadeentreastelas.R
import com.thiagosilva.navegabilidadeentreastelas.data.model.Status
import com.thiagosilva.navegabilidadeentreastelas.data.model.Task
import com.thiagosilva.navegabilidadeentreastelas.databinding.FragmentDoneBinding
import com.thiagosilva.navegabilidadeentreastelas.ui.adapter.TaskAdapter
import com.thiagosilva.navegabilidadeentreastelas.util.FirebaseHelper
import com.thiagosilva.navegabilidadeentreastelas.util.showBottomSheet

class DoneFragment : Fragment() {
    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter
    private val viewModel: TaskViewModel by activityViewModels()
    private var taskListener: ValueEventListener? = null
    private var listUserId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewTask()
        initListener()
        getTask()
    }

    private fun initListener() {
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.taskUpdate.observe(viewLifecycleOwner) { updateTask ->
            if (updateTask.status == Status.DONE) {
                val oldList = taskAdapter.currentList
                val newList = oldList.toMutableList().apply {
                    find { it.id == updateTask.id }?.description = updateTask.description
                }
                val position = newList.indexOfFirst { it.id == updateTask.id }
                taskAdapter.submitList(newList)
                if (position >= 0) taskAdapter.notifyItemChanged(position)
            }
        }
    }

    private fun initRecyclerViewTask() {
        taskAdapter = TaskAdapter(requireContext()) { task, option -> optionSelected(task, option) }
        with(binding.recyclerViewTask) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = taskAdapter
        }
    }

    private fun optionSelected(task: Task, option: Int) {
        when (option) {
            TaskAdapter.SELECT_REMOVER -> {
                showBottomSheet(
                    titleDialog = R.string.text_title_dialog_delete,
                    message = getString(R.string.text_message_dialog_delete),
                    titleButton = R.string.text_button_dialog_confirm,
                    onClick = { deleteTask(task) }
                )
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections.actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_DETAILS -> {
                Toast.makeText(requireContext(), "Detalhes ${task.description}", Toast.LENGTH_SHORT).show()
            }
            TaskAdapter.SELECT_NEXT -> {
                Toast.makeText(requireContext(), "Próximo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTask() {
        listUserId = FirebaseHelper.getIdUser()
        if (listUserId.isBlank()) {
            binding.progressBar.isVisible = false
            return
        }
        binding.progressBar.isVisible = true
        binding.textInfo.setText(R.string.text_list_task_load)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val taskList = mutableListOf<Task>()
                for (ds in snapshot.children) {
                    val task = ds.getValue(Task::class.java)
                    if (task != null && task.status == Status.DONE) {
                        taskList.add(task)
                    }
                }
                binding.progressBar.isVisible = false
                listEmpty(taskList)
                taskList.reverse()
                taskAdapter.submitList(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
                if (_binding == null) return
                binding.progressBar.isVisible = false
                binding.textInfo.text = ""
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        }
        taskListener = listener
        FirebaseHelper.getDatabase()
            .child("task")
            .child(listUserId)
            .addValueEventListener(listener)
    }

    private fun deleteTask(task: Task) {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser())
            .child(task.id)
            .removeValue()
            .addOnCompleteListener { result ->
                if (_binding == null) return@addOnCompleteListener
                Toast.makeText(
                    requireContext(),
                    if (result.isSuccessful) R.string.text_delete_success_task else R.string.error_generic,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun listEmpty(taskList: List<Task>) {
        binding.textInfo.text = if (taskList.isEmpty()) {
            getString(R.string.text_list_task_empty)
        } else {
            ""
        }
    }

    override fun onDestroyView() {
        taskListener?.let {
            FirebaseHelper.getDatabase().child("task").child(listUserId).removeEventListener(it)
        }
        taskListener = null
        binding.recyclerViewTask.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
