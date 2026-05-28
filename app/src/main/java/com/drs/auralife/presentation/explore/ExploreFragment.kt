package com.drs.auralife.presentation.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.databinding.FragmentExploreBinding
import com.drs.auralife.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ExploreFragment : Fragment() {
    private val exploreViewModel: ExploreViewModel by viewModels()
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setupAppBar(binding.appBar)

        exploreViewModel.loadCategories()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            exploreViewModel.categoriesState.collect { categories ->
                if (_binding == null || categories.isEmpty()) return@collect
                buildCategoryViews(categories)
            }
        }
    }

    private fun buildCategoryViews(categories: List<com.drs.auralife.domain.model.Category>) {
        categories.forEach { category ->
            val currentContext = context ?: return@forEach
            val item = layoutInflater.inflate(R.layout.horizontal_film_list, null)

            item
                .findViewById<AppCompatButton>(R.id.buttonHorizontalFilmList)
                .setOnClickListener {
                    startActivity(ExploreDetailsActivity.newInstance(currentContext, category))
                }

            val filmAdapter = CategoryFilmAdapter(mutableListOf())
            item.findViewById<RecyclerView>(R.id.recyclerView).adapter = filmAdapter
            binding.exploreFragmentBody.addView(item)

            val buttonText = if (Locale.getDefault().language == "en") {
                category.localizedName
            } else {
                category.name
            }
            item.findViewById<AppCompatButton>(R.id.buttonHorizontalFilmList).text = buttonText

            exploreViewModel.getFilmsByCategoryList(category.slug, 1) { films ->
                if (_binding == null) return@getFilmsByCategoryList
                films?.let { list ->
                    filmAdapter.addItem(list)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            _binding?.root?.isSelected = true
        }
    }

    override fun onPause() {
        super.onPause()
        _binding?.root?.isSelected = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
