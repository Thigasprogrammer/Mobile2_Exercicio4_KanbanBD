package com.thiagosilva.navegabilidadeentreastelas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thiagosilva.navegabilidadeentreastelas.R
import com.thiagosilva.navegabilidadeentreastelas.data.model.Status
import com.thiagosilva.navegabilidadeentreastelas.data.model.Task
import com.thiagosilva.navegabilidadeentreastelas.databinding.FragmentFormTaskBinding
import com.thiagosilva.navegabilidadeentreastelas.util.FirebaseHelper
import com.thiagosilva.navegabilidadeentreastelas.util.initToolbar
import com.thiagosilva.navegabilidadeentreastelas.util.showBottomSheet

class FormTaskFragment : BaseFragment() {
    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var task: Task
    private var newTask = true
    private var status = Status.TODO
    private val args: FormTaskFragmentArgs by navArgs()
    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        getArgs()
        initListener()
    }

    private fun getArgs() {
        args.task?.let {
            task = it
            configTask()
        }
    }

    private fun configTask() {
        newTask = false
        status = task.status
        binding.textToolbar.setText(R.string.text_title_edit_task_form_task_fragment)
        binding.editTextDescricao.setText(task.description)
        setStatus()
    }

    private fun setStatus() {
        val id = when (task.status) {
            Status.TODO -> R.id.rbTodo
            Status.DOING -> R.id.rbDoing
            Status.DONE -> R.id.rbDone
        }
        binding.radioGroup.check(id)
    }

    private fun initListener() {
        binding.buttonSave.setOnClickListener { validateData() }
        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            status = when (id) {
                R.id.rbTodo -> Status.TODO
                R.id.rbDoing -> Status.DOING
                else -> Status.DONE
            }
        }
    }

    private fun validateData() {
        val description = binding.editTextDescricao.text.toString().trim()
        if (description.isNotBlank()) {
            hideKeyboard()
            binding.progressBar.isVisible = true
            if (newTask) task = Task()
            task.description = description
            task.status = status
            saveTask()
        } else {
            showBottomSheet(message = getString(R.string.description_empty_form_task_fragment))
        }
    }

    private fun saveTask() {
        val userId = FirebaseHelper.getIdUser()
        if (userId.isBlank()) {
            binding.progressBar.isVisible = false
            showBottomSheet(message = getString(R.string.error_generic))
            return
        }
        try {
            FirebaseHelper.getDatabase()
                .child("task")
                .child(userId)
                .child(task.id)
                .setValue(task)
                .addOnCompleteListener { result ->
                    if (_binding == null) return@addOnCompleteListener
                    binding.progressBar.isVisible = false
                    if (result.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            if (newTask) R.string.text_save_success_form_task_fragment
                            else R.string.text_update_success_form_task_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (newTask) {
                            findNavController().popBackStack()
                        } else {
                            viewModel.setUpdateTask(task)
                        }
                    } else {
                        val errorMessage = result.exception?.message ?: getString(R.string.error_generic)
                        showBottomSheet(message = errorMessage)
                    }
                }
        } catch (error: Exception) {
            binding.progressBar.isVisible = false
            showBottomSheet(message = error.message ?: getString(R.string.error_generic))
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
