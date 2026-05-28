package com.drs.auralife.presentation.film.details

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drs.auralife.R
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.databinding.ActivityFilmDetailsBinding
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.presentation.auth.LoginActivity
import com.drs.auralife.presentation.film.SLUG
import com.drs.auralife.presentation.film.play.PlayFilmActivity
import com.drs.auralife.presentation.library.AddToLibrary
import com.drs.auralife.core.utils.MyAppGlideModule
import kotlinx.coroutines.launch

@dagger.hilt.android.AndroidEntryPoint
class FilmDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFilmDetailsBinding.inflate(layoutInflater) }
    private val viewModel: FilmsViewModel by viewModels()
    private var slug: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        slug = intent.getStringExtra(SLUG)

        observeState()
        slug?.let { viewModel.getFilmDetails(it) }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmDetailsState.collect { film ->
                    film?.let { updateUI(it) }
                }
            }
        }
    }

    private fun updateUI(film: FilmDetails) {
        binding.nameFilm.text = film.title
        binding.nameEngFilm.text = film.originName
        binding.filmDescription.text =
            HtmlCompat.fromHtml(film.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

        for ((key, value) in getFilmDetailsMap(film)) {
            findViewById<LinearLayout>(R.id.filmDetails).addView(
                createFilmDetailItem(key, value),
            )
        }

        MyAppGlideModule.loadImage(this, film.posterUrl, binding.posterView)
        MyAppGlideModule.loadImage(this, film.thumbUrl, binding.thumbView)

        binding.trailerButton.setOnClickListener {
            film.trailerUrl?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        binding.playButton.setOnClickListener {
            val intent = Intent(this, PlayFilmActivity::class.java)
            intent.putExtra(SLUG, film.slug)
            startActivity(intent)
        }

        binding.addToLibrary.setOnClickListener {
            if (Authentication.isLoggedIn()) {
                AddToLibrary.showAddLibraryDialog(this, film.slug, film.posterUrl, film.episodeCurrent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getFilmDetailsMap(film: FilmDetails): Map<String, String?> =
        mapOf(
            getString(R.string.status) to film.episodeCurrent,
            getString(R.string.episode_number) to film.episodeTotal,
            getString(R.string.duration) to film.duration,
            getString(R.string.quality) to film.quality,
            getString(R.string.language) to film.language,
            getString(R.string.director) to film.directors?.joinToString(", "),
            getString(R.string.casts) to film.actors?.joinToString(", "),
            getString(R.string.category) to film.categories?.joinToString(", "),
            getString(R.string.year_of_release) to film.year?.toString(),
            getString(R.string.country) to film.countries?.joinToString(", "),
        )

    private fun createFilmDetailItem(
        key: String,
        value: String?,
    ): View {
        val itemDetail = layoutInflater.inflate(R.layout.item_detail, LinearLayout(this))

        itemDetail.findViewById<TextView>(R.id.nameDetail).text = key
        itemDetail.findViewById<TextView>(R.id.valueDetail).text = value ?: "N/A"

        return itemDetail
    }
}
