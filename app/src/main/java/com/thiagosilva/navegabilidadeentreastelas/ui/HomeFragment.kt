package com.thiagosilva.navegabilidadeentreastelas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.thiagosilva.navegabilidadeentreastelas.R
import com.thiagosilva.navegabilidadeentreastelas.databinding.FragmentHomeBinding
import com.thiagosilva.navegabilidadeentreastelas.ui.adapter.ViewPageAdapter
import com.thiagosilva.navegabilidadeentreastelas.util.showBottomSheet

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var tabMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        initListeners()
        initTabs()
    }

    private fun initListeners() {
        binding.btnLogout.setOnClickListener {
            showBottomSheet(
                titleButton = R.string.text_button_dialog_confirm_logout,
                titleDialog = R.string.text_title_dialog_confirm_logout,
                message = getString(R.string.text_message_dialog_confirm_logout),
                onClick = {
                    auth.signOut()
                    findNavController().navigate(R.id.action_homeFragment_to_authentication)
                }
            )
        }
    }

    private fun initTabs() {
        val pageAdapter = ViewPageAdapter(this)
        pageAdapter.addFragment(TodoFragment(), R.string.status_task_todo)
        pageAdapter.addFragment(DoingFragment(), R.string.status_task_doing)
        pageAdapter.addFragment(DoneFragment(), R.string.status_task_done)
        binding.viewPager.adapter = pageAdapter
        binding.viewPager.offscreenPageLimit = pageAdapter.itemCount
        tabMediator = TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = getString(pageAdapter.getTitle(position))
        }.also { it.attach() }
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        binding.viewPager.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
