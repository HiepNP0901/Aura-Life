package com.drs.auralife.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.drs.auralife.data.model.films.Paginate
import com.drs.auralife.databinding.FragmentHomeBinding
import com.drs.auralife.ui.film.FilmFragment
import com.drs.auralife.ui.film.FilmViewModelFactory
import com.drs.auralife.ui.film.FilmsViewModel

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private var isLoading = false

    private lateinit var viewModel: FilmsViewModel

    private lateinit var viewModelFactory: FilmViewModelFactory

    private lateinit var paginate: Paginate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = FilmViewModelFactory(requireContext())

        viewModel = ViewModelProvider(this, viewModelFactory)[FilmsViewModel::class.java]

        viewModel.fetchLatestFilms(1)

        viewModel.fetchLatestFilms(2)

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
        val scrollView = binding.scrollHome

        if (paginate.currentPage < paginate.totalPage) {

            scrollView.viewTreeObserver.addOnScrollChangedListener {

                val view = scrollView.getChildAt(scrollView.childCount - 1)
                val viewSize = view.bottom

                if (view != null) {

                    val diff = viewSize - (scrollView.height + scrollView.scrollY)

                    if (diff <= 200 && !isLoading) {

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

                films.items.forEach { item ->
                    val fragmentContainer = FrameLayout(requireContext()).apply {
                        id = View.generateViewId()
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = 0
                            height = GridLayout.LayoutParams.WRAP_CONTENT
                            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                            setMargins(8, 8, 8, 8)
                        }
                    }

                    binding.gridLayout.addView(fragmentContainer)

                    childFragmentManager.beginTransaction()
                        .replace(fragmentContainer.id, FilmFragment.Companion.newInstance(item))
                        .commit()
                }

                isLoading = false
            }
        })
    }
}