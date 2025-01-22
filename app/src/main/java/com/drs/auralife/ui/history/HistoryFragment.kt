package com.drs.auralife.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drs.auralife.R
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.history.History
import com.drs.auralife.data.firebase.history.HistoryRepository
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.databinding.FragmentLibraryBinding
import com.drs.auralife.ui.MainActivity
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.film.HORIZONTAL
import com.drs.auralife.utils.HistoryUtils
import com.drs.auralife.utils.Time
import java.time.Instant

class HistoryFragment : Fragment(), FilmAdapter.FragmentListener {
    private val binding by lazy { FragmentLibraryBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(
            this, FilmViewModelFactory(requireContext())
        )[FilmsViewModel::class.java]
    }
    private val filmAdapter = FilmAdapter(mutableListOf(), HORIZONTAL)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).setupAppBar(binding.appBar)
        binding.appBar.findViewById<ImageButton>(R.id.app_bar_search).visibility = View.GONE
        filmAdapter.setCallback(this)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshHistory()
    }

    fun refreshHistory() {
        if (Authentication.isLoggedIn()) {
            HistoryRepository.getHistoryData { listHistory ->
                refreshRecyclerView(listHistory)
            }
        }
        else {
            val listHistory = HistoryUtils.getHistories(requireContext())
            refreshRecyclerView(listHistory)
        }
    }

    private fun refreshRecyclerView(listHistory: List<History>) {
        if (listHistory.isEmpty()) {
            binding.text.visibility = View.VISIBLE
        }
        else {
            binding.text.visibility = View.GONE
            binding.recyclerView.adapter = filmAdapter
            val tempList = mutableListOf<Movie>()
            listHistory.forEach{ history ->
                viewModel.fetchFilmDetails(history.slug){
                    it?.movie?.let {movie ->
                        movie.content = Time.calculateTimeDifference(
                            Instant.ofEpochMilli(
                                history.date.toLong()
                            ), requireContext()
                        ) + "<br>" + movie.content

                        tempList.add(movie)
                    }
                    if (tempList.size == listHistory.size) {
                        val sortedList = listHistory.mapNotNull { h ->
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