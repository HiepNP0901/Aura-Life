package com.drs.auralife.ui.fragmentPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.data.model.films.FilmPreviews
import com.drs.auralife.data.model.films.Paginate
import com.drs.auralife.databinding.FragmentHomeBinding
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.film.FilmViewModelFactory
import com.drs.auralife.ui.film.FilmsViewModel

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var itemAdapter: FilmAdapter

    private var isLoading = false

    private lateinit var viewModel: FilmsViewModel

    private lateinit var viewModelFactory: FilmViewModelFactory

    private lateinit var paginate: Paginate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView = binding.recyclerView

        viewModelFactory = FilmViewModelFactory(requireContext())

        viewModel = ViewModelProvider(this, viewModelFactory)[FilmsViewModel::class.java]

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        viewModel.fetchLatestFilms(1)
        viewModel.fetchLatestFilms(2)

        val item: MutableList<FilmPreviews> = mutableListOf()
        item.addAll(viewModel.films.value?.items ?: emptyList())

        itemAdapter = FilmAdapter(item)

        recyclerView.adapter = itemAdapter

        addFragmentFilm()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


    private fun loadMoreFilm() {
        if (paginate.currentPage < paginate.totalPage) {

            recyclerView.viewTreeObserver.addOnScrollChangedListener {

                val view = recyclerView.getChildAt(recyclerView.childCount - 1)

                val viewSize = view.bottom

                if (view != null) {

                    val diff = viewSize - (recyclerView.height + recyclerView.scrollY)

                    if (diff <= 20 && !isLoading) {

                        isLoading = true

                        viewModel.fetchLatestFilms(paginate.currentPage + 1)
                    }
                }
            }
        }
    }


    private fun addFragmentFilm() {
        viewModel.films.observe(this, Observer { films ->
            if (films != null) {

                paginate = films.paginate

                loadMoreFilm()

                itemAdapter.addItem(films.items)

                isLoading = false
            }
        })
    }
}