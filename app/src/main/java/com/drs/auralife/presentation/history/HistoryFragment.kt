package com.drs.auralife.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.R
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.realtime.database.user.history.History
import com.drs.auralife.data.firebase.realtime.database.user.history.HistoryRepository
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.databinding.FragmentLibraryBinding
import com.drs.auralife.presentation.MainActivity
import com.drs.auralife.presentation.film.FilmAdapter
import com.drs.auralife.presentation.film.HORIZONTAL
import com.drs.auralife.core.utils.HistoryUtils
import com.drs.auralife.core.utils.Time
import java.time.Instant

@dagger.hilt.android.AndroidEntryPoint
class HistoryFragment :
    Fragment(),
    FilmAdapter.FragmentListener {
    private val viewModel: FilmsViewModel by viewModels()
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val filmAdapter = FilmAdapter(mutableListOf(), HORIZONTAL)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setupAppBar(binding.appBar)
        binding.appBar.findViewById<ImageButton>(R.id.app_bar_search).visibility = View.GONE
        filmAdapter.setCallback(this)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun refreshHistory() {
        if (Authentication.isLoggedIn()) {
            HistoryRepository.getHistoryData { listHistory ->
                if (_binding == null) return@getHistoryData
                refreshRecyclerView(listHistory)
            }
        } else {
            context?.applicationContext?.let {
                val listHistory = HistoryUtils.getLocalHistories(it)
                refreshRecyclerView(listHistory)
            }
        }
    }

    private fun refreshRecyclerView(listHistory: List<History>) {
        if (listHistory.isEmpty()) {
            binding.text.visibility = View.VISIBLE
            filmAdapter.clearItems()
            binding.recyclerView.adapter = filmAdapter
        } else {
            binding.text.visibility = View.GONE
            binding.recyclerView.adapter = filmAdapter
            val tempList = mutableListOf<Movie>()
            listHistory.forEach { history ->
                val currentContext = context ?: return@forEach
                viewModel.fetchFilmDetailsLegacy(history.slug) { filmDetails: com.drs.auralife.data.model.film.FilmDetails? ->
                    if (_binding == null) return@fetchFilmDetailsLegacy
                    filmDetails?.movie?.let { movie ->
                        context?.applicationContext?.let { appContext ->
                            movie.content = Time.calculateTimeDifference(
                                Instant.ofEpochMilli(
                                    history.date.toLong(),
                                ),
                                appContext,
                            ) + "<br>" + movie.content
                        }

                        tempList.add(movie)
                    }
                    if (tempList.size == listHistory.size) {
                        val sortedList = listHistory
                            .mapNotNull { h ->
                                tempList.find { it.slug == h.slug }
                            }.reversed()
                        filmAdapter.replaceItems(sortedList)
                    }
                }
            }
        }
    }

    override fun onLongClick(slug: String) {
        DeleteHistory.showDeleteFilmFromHistory(requireContext(), slug) {
            filmAdapter.removeItem(slug)
        }
    }
}

