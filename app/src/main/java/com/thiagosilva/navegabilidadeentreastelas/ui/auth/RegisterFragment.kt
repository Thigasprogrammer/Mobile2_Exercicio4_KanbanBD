package com.thiagosilva.navegabilidadeentreastelas.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.thiagosilva.navegabilidadeentreastelas.R
import com.thiagosilva.navegabilidadeentreastelas.databinding.FragmentRegisterBinding
import com.thiagosilva.navegabilidadeentreastelas.ui.BaseFragment
import com.thiagosilva.navegabilidadeentreastelas.util.FirebaseHelper
import com.thiagosilva.navegabilidadeentreastelas.util.initToolbar
import com.thiagosilva.navegabilidadeentreastelas.util.showBottomSheet

class RegisterFragment : BaseFragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListener()
    }

    private fun initListener() {
        binding.registrar.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val email = binding.email.text.toString().trim()
        val password = binding.senha.text.toString().trim()
        if (email.isNotBlank()) {
            if (password.isNotBlank()) {
                hideKeyboard()
                binding.progressBar.isVisible = true
                registerUser(email, password)
            } else {
                showBottomSheet(message = getString(R.string.password_empty_register_fragment))
            }
        } else {
            showBottomSheet(message = getString(R.string.email_empty_register_fragment))
        }
    }

    private fun registerUser(email: String, password: String) {
        try {
            FirebaseHelper.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (_binding == null) return@addOnCompleteListener
                    binding.progressBar.isVisible = false
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_global_homeFragment)
                    } else {
                        showBottomSheet(message = getString(
                            FirebaseHelper.validError(task.exception?.message.orEmpty())
                        ))
                    }
                }
        } catch (error: Exception) {
            binding.progressBar.isVisible = false
            showBottomSheet(message = getString(FirebaseHelper.validError(error.message.orEmpty())))
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
