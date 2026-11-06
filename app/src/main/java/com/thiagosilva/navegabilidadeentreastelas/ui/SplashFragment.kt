package com.thiagosilva.navegabilidadeentreastelas.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.thiagosilva.navegabilidadeentreastelas.R
import com.thiagosilva.navegabilidadeentreastelas.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val handler = Handler(Looper.getMainLooper())
    private val checkAuthRunnable = Runnable {
        if (_binding != null) checkAuth()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        handler.postDelayed(checkAuthRunnable, 3000)
    }

    private fun checkAuth() {
        try {
            if (auth.currentUser != null) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_authentication)
            }
        } catch (error: Exception) {
            Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_splashFragment_to_authentication)
        }
    }

    override fun onDestroyView() {
        handler.removeCallbacks(checkAuthRunnable)
        _binding = null
        super.onDestroyView()
    }
}
