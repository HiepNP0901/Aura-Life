package com.drs.auralife.presentation.film.details

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import com.drs.auralife.R
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.databinding.ActivityFilmDetailsBinding
import com.drs.auralife.presentation.auth.LoginActivity
import com.drs.auralife.presentation.film.SLUG
import com.drs.auralife.presentation.film.play.PlayFilmActivity
import com.drs.auralife.presentation.library.AddToLibrary
import com.drs.auralife.core.utils.MyAppGlideModule

class FilmDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFilmDetailsBinding.inflate(layoutInflater) }
    private var slug: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        slug = intent.getStringExtra(SLUG)

        updateUI()
    }

    private fun updateUI() {
        slug?.let {
            FilmsViewModel(this).fetchFilmDetailsLegacy(it) { film: com.drs.auralife.data.model.film.FilmDetails? ->
                film?.let {
                    binding.nameFilm.text = it.movie.name
                    binding.nameEngFilm.text = it.movie.originName
                    binding.filmDescription.text =
                        HtmlCompat.fromHtml(it.movie.content.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)

                    for ((key, value) in getFilmDetailsMap(film.movie)) {
                        findViewById<LinearLayout>(R.id.filmDetails).addView(
                            createFilmDetailItem(key, value),
                        )
                    }

                    MyAppGlideModule.loadImage(this, film.movie.posterUrl, binding.posterView)
                    MyAppGlideModule.loadImage(this, film.movie.thumbUrl, binding.thumbView)

                    binding.trailerButton.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, film.movie.trailerUrl?.toUri())
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }

                    binding.playButton.setOnClickListener {
                        val intent = Intent(this, PlayFilmActivity::class.java)
                        intent.putExtra(SLUG, slug)
                        startActivity(intent)
                    }

                    binding.addToLibrary.setOnClickListener {
                        if (Authentication.isLoggedIn()) {
                            AddToLibrary.showAddLibraryDialog(this, film.movie)
                        } else {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun getFilmDetailsMap(movie: Movie): Map<String, String?> =
        mapOf(
            getString(R.string.status) to movie.episodeCurrent,
            getString(R.string.episode_number) to movie.episodeTotal,
            getString(R.string.duration) to movie.time,
            getString(R.string.quality) to movie.quality,
            getString(R.string.language) to movie.lang,
            getString(R.string.director) to movie.director?.joinToString(", "),
            getString(R.string.casts) to movie.actor?.joinToString(", "),
            getString(R.string.category) to movie.category?.joinToString(", ") { it.name },
            getString(R.string.year_of_release) to movie.year.toString(),
            getString(R.string.country) to movie.country?.joinToString(", ") { it.name },
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

