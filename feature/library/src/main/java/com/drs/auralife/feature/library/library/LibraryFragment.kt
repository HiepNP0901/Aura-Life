package com.drs.auralife.feature.library.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.feature.library.R
import com.drs.auralife.core.designsystem.R as DsR
import com.drs.auralife.navigation.NavRoutes
import com.drs.auralife.feature.library.databinding.FragmentLibraryBinding
import com.drs.auralife.designsystem.AppBarProvider
import com.drs.auralife.feature.library.library.adapter.LibraryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")
    private val libraryViewModel: LibraryViewModel by viewModels()
    private lateinit var libraryAdapter: LibraryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        (requireActivity() as AppBarProvider).setupAppBar(binding.appBar)
        binding.appBar.findViewById<ImageButton>(DsR.id.app_bar_search).visibility = View.GONE
        binding.appBar.findViewById<ImageButton>(DsR.id.app_bar_notifications).visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryAdapter = LibraryAdapter(
            onRename = { oldName, newName -> libraryViewModel.renameLibrary(oldName, newName) },
            onDelete = { name -> libraryViewModel.deleteLibrary(name) },
            onItemClick = { name -> libraryViewModel.onLibraryClicked(name) },
        )
        binding.recyclerView.adapter = libraryAdapter

        observeLibraries()
        observeEffect()
    }

    override fun onResume() {
        super.onResume()
        refreshLibrary()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLibraries() {
        launchAndRepeatWithViewLifecycle {
                libraryViewModel.librariesState.collect { state ->
                    if (_binding == null) return@collect
                    binding.loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    libraryAdapter.submitList(state.libraries)

                    if (!libraryViewModel.isLoggedIn()) {
                        binding.text.visibility = View.VISIBLE
                        binding.text.text = getString(R.string.function_must_login)
                    } else if (state.libraries.isEmpty()) {
                        binding.text.visibility = View.VISIBLE
                        binding.text.text = getString(R.string.empty)
                    } else {
                        binding.text.visibility = View.GONE
                    }
                    if (state.errorMessage != null) {
                        binding.text.visibility = View.VISIBLE
                        binding.text.text = state.errorMessage
                    }
                }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                libraryViewModel.effect.collect { effect ->
                    when (effect) {
                        is LibraryUiEffect.ShowToast -> {
                            Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is LibraryUiEffect.NavigateToDetails -> {
                            findNavController().navigate(NavRoutes.libraryDetails(effect.name))
                        }
                    }
                }
        }
    }

    private fun refreshLibrary() {
        libraryViewModel.getLibraries()
    }
}

