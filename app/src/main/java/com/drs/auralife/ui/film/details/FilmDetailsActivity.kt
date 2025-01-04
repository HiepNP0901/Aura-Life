package com.drs.auralife.ui.film.details

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.drs.auralife.R
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.model.movie.Movie
import com.drs.auralife.ui.film.play.PlayFilmActivity
import com.drs.auralife.ui.home.SLUG
import jp.wasabeef.glide.transformations.BlurTransformation

class FilmDetailsActivity : AppCompatActivity() {
    private lateinit var viewModel: FilmsViewModel
    private var slug: String? = null

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_film_details)

        viewModel = ViewModelProvider(this, FilmViewModelFactory(this))[FilmsViewModel::class.java]

        slug = intent.getStringExtra(SLUG)
    }

    override fun onStart() {
        super.onStart()
        slug?.let {
            viewModel.fetchFilmDetails(it) {
                it?.let {film ->
                    Glide.with(this)
                        .load(film.movie.thumbUrl)
                        .placeholder(R.drawable.bg_logo)
                        .error(R.drawable.rounded)
                        .into(findViewById(R.id.imageFilm))

                    Glide.with(this)
                        .load(film.movie.posterUrl)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 3)))
                        .error(R.drawable.rounded)
                        .into(findViewById(R.id.bannerView))

                    findViewById<TextView>(R.id.nameFilm).text = film.movie.name

                    findViewById<TextView>(R.id.nameEngFilm).text = film.movie.originalName

                    for ((key, value) in getFilmMap(film.movie)) {
                        findViewById<LinearLayout>(R.id.filmDetails).addView(
                            createFilmDetailItem(key, value)
                        )
                    }

                    findViewById<TextView>(R.id.filmDescription).text = film.movie.description

                    findViewById<Button>(R.id.playButton).setOnClickListener {
                        val intent = Intent(this, PlayFilmActivity::class.java)
                        intent.putExtra(SLUG, film.movie.slug)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun createFilmDetailItem(key: String, value: String?): View {
        val itemDetail = layoutInflater.inflate(R.layout.item_detail, null)

        itemDetail.findViewById<TextView>(R.id.nameDetail).text = key
        itemDetail.findViewById<TextView>(R.id.valueDetail).text = value ?: "N/A"

        return itemDetail
    }

    fun getFilmMap(movie: Movie): Map<String, String?> {
        return mapOf(
            getString(R.string.status) to movie.currentEpisode,
            getString(R.string.episode_number) to movie.totalEpisodes.toString(),
            getString(R.string.duration) to movie.time,
            getString(R.string.quality) to movie.quality,
            getString(R.string.language) to movie.language,
            getString(R.string.director) to movie.director,
            getString(R.string.casts) to movie.casts,
            getString(R.string.format) to movie.category.category1.list.joinToString(", ") { it.name },
            getString(R.string.category) to movie.category.category2.list.joinToString(", ") { it.name },
            getString(R.string.year_of_release) to movie.category.category3.list.joinToString(", ") { it.name },
            getString(R.string.country) to movie.category.category4.list.joinToString(", ") { it.name }
        )
    }
}