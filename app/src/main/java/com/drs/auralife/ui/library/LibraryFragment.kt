package com.drs.auralife.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drs.auralife.R
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.library.LibraryRepository
import com.drs.auralife.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {
    val binding by lazy { FragmentLibraryBinding.inflate(layoutInflater) }
    val libraryAdapter = LibraryAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshLibrary()
    }

    fun refreshLibrary() {
        binding.recyclerView.adapter = libraryAdapter
        LibraryRepository().getLibrary {
            libraryAdapter.refreshLibrary(it)

            if (!Authentication.isLoggedIn()) {
                binding.text.visibility = View.VISIBLE
                binding.text.text = getString(R.string.function_must_login)
            }
            else if(it.isEmpty()){
                binding.text.visibility = View.VISIBLE
                binding.text.text = getString(R.string.empty)
            }
            else {
                binding.text.visibility = View.GONE
            }
        }

    }
}