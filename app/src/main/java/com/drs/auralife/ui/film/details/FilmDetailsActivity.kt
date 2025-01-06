package com.drs.auralife.ui.film.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drs.auralife.R
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.databinding.ActivityFilmDetailsBinding
import com.drs.auralife.ui.film.play.PlayFilmActivity
import com.drs.auralife.ui.home.SLUG
import com.drs.auralife.utils.MyAppGlideModule
import jp.wasabeef.glide.transformations.BlurTransformation

class FilmDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilmDetailsBinding
    private lateinit var viewModel: FilmsViewModel
    private var slug: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilmDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, FilmViewModelFactory(this))[FilmsViewModel::class.java]

        slug = intent.getStringExtra(SLUG)

        if (slug == null) {
            Toast.makeText(this, "Invalid film data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        else {
            binding.playButton.setOnClickListener {
                val intent = Intent(this, PlayFilmActivity::class.java)
                intent.putExtra(SLUG, slug)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        slug?.let { loadFilmDetails(it) }
    }

    private fun loadFilmDetails(slug: String) {
        viewModel.fetchFilmDetails(slug) { film ->
            film?.let { updateUI(it.movie) }
        }
    }

    private fun updateUI(movie: Movie) {
        MyAppGlideModule.loadImage(this, movie.posterUrl, binding.posterView)
        MyAppGlideModule.loadImage(this, movie.thumbUrl, binding.thumbView, BlurTransformation())

        binding.nameFilm.text = movie.name
        binding.nameEngFilm.text = movie.originName
        binding.filmDescription.text = movie.content

        for ((key, value) in getFilmMap(movie)) {
            findViewById<LinearLayout>(R.id.filmDetails).addView(
                createFilmDetailItem(key, value)
            )
        }

        binding.trailerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(movie.trailerUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun createFilmDetailItem(key: String, value: String?): View {
        val itemDetail = layoutInflater.inflate(R.layout.item_detail, LinearLayout(this))

        itemDetail.findViewById<TextView>(R.id.nameDetail).text = key
        itemDetail.findViewById<TextView>(R.id.valueDetail).text = value ?: "N/A"

        return itemDetail
    }

    private fun getFilmMap(movie: Movie): Map<String, String> {
        return mapOf(
            getString(R.string.status) to movie.episodeCurrent,
            getString(R.string.episode_number) to movie.episodeTotal,
            getString(R.string.duration) to movie.time,
            getString(R.string.quality) to movie.quality,
            getString(R.string.language) to movie.lang,
            getString(R.string.director) to movie.director.joinToString(", "),
            getString(R.string.casts) to movie.actor.joinToString(", "),
            getString(R.string.category) to movie.category.joinToString(", ") { it.name },
            getString(R.string.year_of_release) to movie.year.toString(),
            getString(R.string.country) to movie.country.joinToString(", ") { it.name }
        )
    }
}
