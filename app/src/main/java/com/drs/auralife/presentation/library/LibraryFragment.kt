package com.drs.auralife.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.R
import com.drs.auralife.databinding.FragmentLibraryBinding
import com.drs.auralife.domain.repository.AuthRepository
import com.drs.auralife.presentation.AppBarProvider
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel: LibraryViewModel by viewModels()
    private lateinit var libraryAdapter: LibraryAdapter

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        (requireActivity() as AppBarProvider).setupAppBar(binding.appBar)
        binding.appBar.findViewById<ImageButton>(R.id.app_bar_search).visibility = View.GONE
        binding.appBar.findViewById<ImageButton>(R.id.app_bar_notifications).visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryAdapter = LibraryAdapter(
            library = mutableListOf(),
            onRename = { oldName, newName ->
                libraryViewModel.renameLibrary(oldName, newName)
            },
            onDelete = { name ->
                libraryViewModel.deleteLibrary(name)
            },
        )
        binding.recyclerView.adapter = libraryAdapter

        observeLibraries()
        observeOperationResult()
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
        lifecycleScope.launch {
            libraryViewModel.librariesState.collect { state ->
                if (_binding == null) return@collect
                when (state) {
                    is UiState.Success -> {
                        val libraries = state.data
                        libraryAdapter.refreshLibrary(libraries.toMutableList())

                        if (!authRepository.isLoggedIn()) {
                            binding.text.visibility = View.VISIBLE
                            binding.text.text = getString(R.string.function_must_login)
                        } else if (libraries.isEmpty()) {
                            binding.text.visibility = View.VISIBLE
                            binding.text.text = getString(R.string.empty)
                        } else {
                            binding.text.visibility = View.GONE
                        }
                    }
                    is UiState.Error -> {
                        binding.text.visibility = View.VISIBLE
                        binding.text.text = state.message
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

    private fun observeOperationResult() {
        lifecycleScope.launch {
            libraryViewModel.operationResult.collect { result ->
                result.onSuccess {
                    refreshLibrary()
                }.onFailure { e ->
                    Toast.makeText(requireContext(), e.message ?: getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun refreshLibrary() {
        libraryViewModel.getLibraries()
    }
}
