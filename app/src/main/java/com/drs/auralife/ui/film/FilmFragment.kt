package com.drs.auralife.ui.film

import android.R.attr.radius
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.drs.auralife.data.model.films.FilmPreviews
import com.drs.auralife.databinding.FragmentFilmBinding
import com.drs.auralife.R

private const val SLUG = "@id"
private const val NAME = "@name_film"
private const val CURRENT_EPISODE = "@username"
private const val THUMB_URL = "@thumb_url"

class FilmFragment : Fragment() {

    private val binding: FragmentFilmBinding by lazy {
        FragmentFilmBinding.inflate(layoutInflater)
    }

    private var slug: String? = null
    private var name: String? = null
    private var currentEpisode: String? = null
    private var thumbUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            slug = it.getString(SLUG)
            name = it.getString(NAME)
            currentEpisode = it.getString(CURRENT_EPISODE)
            thumbUrl = it.getString(THUMB_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.nameFilm.text = name

        binding.detailsFilm.text = currentEpisode

        Glide.with(this).load(thumbUrl).transform(RoundedCorners(16)).into(binding.imageFilm)

        binding.filmButton.setOnClickListener {
            // TODO: Handle film button click
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(film: FilmPreviews): FilmFragment {
            val fragment = FilmFragment()
            val args = Bundle()
            args.putString(SLUG, film.slug)
            args.putString(NAME, film.name)
            args.putString(THUMB_URL, film.thumbUrl)
            args.putString(CURRENT_EPISODE, film.currentEpisode)
            fragment.arguments = args
            return fragment
        }
    }
}