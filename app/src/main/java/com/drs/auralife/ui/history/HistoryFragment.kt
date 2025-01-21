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
import com.drs.auralife.data.firebase.RealtimeDB
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.databinding.FragmentLibraryBinding
import com.drs.auralife.ui.MainActivity
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.film.HORIZONTAL

class HistoryFragment : Fragment() {
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
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshLibrary()
    }

    fun refreshLibrary() {
        RealtimeDB.getHistoryData(requireContext()) { listHistory ->
            binding.recyclerView.adapter = filmAdapter
            val tempList = mutableListOf<Movie>()
            listHistory.forEach{ history ->
                viewModel.fetchFilmDetails(history.slug){
                    it?.movie?.let {movie ->
                        movie.content = history.date.toString() + "<br>" + movie.content
                        tempList.add(movie)
                    }
                    if (tempList.size == listHistory.size) {
                        val sortedList = listHistory.mapNotNull { h ->
                            tempList.find { it.slug == h.slug }
                        }
                        filmAdapter.replaceItems(sortedList)
                    }
                }
            }

            if (!Authentication.isLoggedIn()) {
                binding.text.visibility = View.VISIBLE
                binding.text.text = getString(R.string.function_must_login)
            }
            else if(listHistory.isEmpty()){
                binding.text.visibility = View.VISIBLE
                binding.text.text = getString(R.string.empty)
            }
            else {
                binding.text.visibility = View.GONE
            }
        }
    }
}