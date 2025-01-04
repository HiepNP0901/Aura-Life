package com.drs.auralife.ui.film.play

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.CustomViewCallback
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.model.FilmDetails
import com.drs.auralife.ui.home.SLUG

const val PLAY_FILM_STATE="@playFilmState"
const val CURRENT_EPISODE="@currentEpisode"

@Suppress("DEPRECATION")
class PlayFilmActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: FilmsViewModel
    private lateinit var webView: WebView
    private lateinit var nameFilm: TextView
    private lateinit var film: FilmDetails
    private lateinit var currentEpisode: String
    private var slug: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_film)

        slug = intent.getStringExtra(SLUG)

        viewModel = ViewModelProvider(this, FilmViewModelFactory(this))[FilmsViewModel::class.java]

        webView = findViewById(R.id.web_view)

        nameFilm = findViewById<TextView>(R.id.nameFilm)

        recyclerView = findViewById(R.id.episodeRecyclerView)

        val layoutManager = GridLayoutManager(this, 4)
        layoutManager.orientation = GridLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager

        setFilmDetail{
            it?.let{
                film = it
                webView.loadUrl(it.movie.episodes[0].items[0].embed)
                @SuppressLint("SetTextI18n")
                nameFilm.text = "${film.movie.name} - Tập ${it.movie.episodes[0].items[0].name}"
                currentEpisode = it.movie.episodes[0].items[0].name
            }
        }

        settingsWebView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val webViewState = Bundle()
        webView.saveState(webViewState)
        outState.putBundle(PLAY_FILM_STATE, webViewState)
        outState.putString(CURRENT_EPISODE, currentEpisode)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        setFilmDetail{
            film = it!!
            if (savedInstanceState.getString(CURRENT_EPISODE) != null) {
                currentEpisode = savedInstanceState.getString(CURRENT_EPISODE).toString()
                webView.loadUrl(film.movie.episodes[0].items.let { it ->
                    it.find { it.name == currentEpisode }?.embed ?: it[0].embed
                })
                @SuppressLint("SetTextI18n")
                nameFilm.text = "${film.movie.name} - Tập $currentEpisode"
            }
        }

        settingsWebView()

        savedInstanceState.let {
            it.getBundle(PLAY_FILM_STATE)?.let { state ->
                webView.restoreState(state)
            }
        }
    }


    private fun setFilmDetail(callback: (FilmDetails?) -> Unit){
        slug?.let {
            viewModel.fetchFilmDetails(it) {
                it?.let {film ->
                    val episodes = film.movie.episodes[0].items.map { it }
                    val viewModel = ViewModelProvider(this)[SelectedItemViewModel::class.java]

                    viewModel.selectedItem.observe(this) { currentEpisode ->
                        webView.loadUrl(currentEpisode.embed)
                        @SuppressLint("SetTextI18n")
                        nameFilm.text = "${film.movie.name} - Tập ${currentEpisode.name}"
                        this.currentEpisode = currentEpisode.name
                    }

                    recyclerView.adapter = EpisodeAdapter(episodes, viewModel)

                    callback(film)
                }
            }
        }
    }


    private fun settingsWebView(){
        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true

        if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            webView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        else {
            webView.layoutParams.height = 700
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                view?.let {
                    val parent = webView.parent as ViewGroup
                    parent.removeView(webView)
                    parent.addView(it)
                }
            }

            @SuppressLint("SourceLockedOrientationActivity")
            override fun onHideCustomView() {
                super.onHideCustomView()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}