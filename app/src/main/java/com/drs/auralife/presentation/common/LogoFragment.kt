package com.drs.auralife.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drs.auralife.databinding.FragmentLogoBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TITLE = "@string/name"

@AndroidEntryPoint
class LogoFragment : Fragment() {
    private var title: String? = null
    private var _binding: FragmentLogoBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLogoBinding.inflate(inflater, container, false)
        binding.textTitle.text = title
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun setTitle(title: String) =
            LogoFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                }
            }
    }
}
