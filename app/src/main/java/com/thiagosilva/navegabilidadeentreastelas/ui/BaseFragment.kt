package com.thiagosilva.navegabilidadeentreastelas.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    fun hideKeyboard() {
        val focusedView = activity?.currentFocus
        if (focusedView != null) {
            val manager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE)
                as? InputMethodManager
            manager?.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }
}
