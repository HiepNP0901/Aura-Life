package com.drs.auralife.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.drs.auralife.R
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.realtime.database.user.library.LibraryRepository
import com.drs.auralife.databinding.FragmentLibraryBinding
import com.drs.auralife.ui.MainActivity

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    val libraryAdapter = LibraryAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setupAppBar(binding.appBar)
        binding.appBar.findViewById<ImageButton>(R.id.app_bar_search).visibility = View.GONE
        binding.appBar.findViewById<ImageButton>(R.id.app_bar_notifications).visibility = View.VISIBLE
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshLibrary()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun refreshLibrary() {
        binding.recyclerView.adapter = libraryAdapter
        LibraryRepository.getLibrary {
            if (_binding == null) return@getLibrary
            libraryAdapter.refreshLibrary(it)

            if (!Authentication.isLoggedIn()) {
                binding.text.visibility = View.VISIBLE
                binding.text.text = getString(R.string.function_must_login)
            } else if (it.isEmpty()) {
                binding.text.visibility = View.VISIBLE
                binding.text.text = getString(R.string.empty)
            } else {
                binding.text.visibility = View.GONE
            }
        }
    }
}
