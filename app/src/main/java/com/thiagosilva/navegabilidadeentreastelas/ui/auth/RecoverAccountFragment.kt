package com.thiagosilva.navegabilidadeentreastelas.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.thiagosilva.navegabilidadeentreastelas.R
import com.thiagosilva.navegabilidadeentreastelas.databinding.FragmentRecoverAccountBinding
import com.thiagosilva.navegabilidadeentreastelas.ui.BaseFragment
import com.thiagosilva.navegabilidadeentreastelas.util.FirebaseHelper
import com.thiagosilva.navegabilidadeentreastelas.util.initToolbar
import com.thiagosilva.navegabilidadeentreastelas.util.showBottomSheet

class RecoverAccountFragment : BaseFragment() {
    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListener()
    }

    private fun initListener() {
        binding.enviar.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val email = binding.email.text.toString().trim()
        if (email.isNotBlank()) {
            hideKeyboard()
            binding.progressBar.isVisible = true
            recoverAccountUser(email)
        } else {
            showBottomSheet(message = getString(R.string.email_empty))
        }
    }

    private fun recoverAccountUser(email: String) {
        try {
            FirebaseHelper.getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (_binding == null) return@addOnCompleteListener
                    binding.progressBar.isVisible = false
                    if (task.isSuccessful) {
                        showBottomSheet(message = getString(R.string.text_message_recover_account_fragment))
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
