package com.drs.auralife.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drs.auralife.databinding.FragmentLogoBinding

private const val TITLE = "@string/name"

class LogoFragment : Fragment() {
    private var title: String? = null
    private val binding: FragmentLogoBinding by lazy {
        FragmentLogoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.textTitle.text = title
        return binding.root
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