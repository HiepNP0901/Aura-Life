package com.drs.auralife.ui.explore

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.realtime.database.category.CategoryRepository
import com.drs.auralife.databinding.FragmentExploreBinding
import com.drs.auralife.ui.MainActivity
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.film.VERTICAL
import java.util.Locale

class ExploreFragment : Fragment() {
    private val binding by lazy { FragmentExploreBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(
            this, FilmViewModelFactory(requireContext())
        )[FilmsViewModel::class.java]
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).setupAppBar(binding.appBar)

        CategoryRepository.getCategoryData {
            it.forEach { category ->
                val item = layoutInflater.inflate(R.layout.horizontal_film_list, null)

                item.findViewById<AppCompatButton>(R.id.buttonHorizontalFilmList)
                    .setOnClickListener {
                        val intent = Intent(
                            requireContext(), ExploreDetailsActivity::class.java
                        )
                        intent.putExtra(CATEGORY_SLUG, category.slug)
                        startActivity(intent)
                    }

                val filmAdapter = FilmAdapter(mutableListOf(), VERTICAL)
                item.findViewById<RecyclerView>(R.id.recyclerView).adapter = filmAdapter
                binding.exploreFragmentBody.addView(item)

                viewModel.fetchFilmsByCategory(category.slug, 1) {
                    it?.data?.let {
                        for (movie in it.items) {
                            movie.posterUrl = it.appDomainCdnImage + "/" + movie.posterUrl
                            movie.thumbUrl = it.appDomainCdnImage + "/" + movie.thumbUrl
                        }

                        filmAdapter.addItem(it.items)

                        if (Locale.getDefault().language == "en") {
                            item.findViewById<AppCompatButton>(R.id.buttonHorizontalFilmList).text =
                                category.en
                        }
                        else if (Locale.getDefault().language == "vi") {
                            item.findViewById<AppCompatButton>(R.id.buttonHorizontalFilmList).text =
                                category.vi
                        }
                    }
                }
            }
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            binding.root.isSelected = true
        }, 3000)
    }


    override fun onPause() {
        super.onPause()
        binding.root.isSelected = false
    }
}