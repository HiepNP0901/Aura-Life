package com.drs.auralife.presentation.playfilm

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.R
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.presentation.filmdetails.FilmDetailsViewModel
import com.drs.auralife.presentation.history.HistoryViewModel
import com.drs.auralife.presentation.playfilm.adapter.EpisodeAdapter
import com.drs.auralife.core.utils.SystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayFilmFragment : Fragment() {

    private val filmDetailsViewModel: FilmDetailsViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val playFilmViewModel: PlayFilmViewModel by viewModels()

    private var exoPlayer: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var nameFilm: TextView? = null
    private var btnPrevious: ImageButton? = null
    private var btnRewind: ImageButton? = null
    private var btnPlayPause: ImageButton? = null
    private var btnForward: ImageButton? = null
    private var btnNext: ImageButton? = null
    private var fullscreenButton: ImageButton? = null
    private var rotateButton: ImageButton? = null

    private var numberEpInLine = 3
    private var currentEpisode = 0
    private var currentPosition: Long = 0
    private var isFullscreen = false
    private var slug: String? = null
    private var film: FilmDetails? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_play_film, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slug = requireArguments().getString("slug")

        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        playerView = view.findViewById(R.id.player_view)
        playerView?.player = exoPlayer

        btnPlayPause = view.findViewById(R.id.btn_play_pause)
        btnRewind = view.findViewById(R.id.btn_rewind)
        btnForward = view.findViewById(R.id.btn_forward)
        btnPrevious = view.findViewById(R.id.btn_previous)
        btnNext = view.findViewById(R.id.btn_next)
        fullscreenButton = view.findViewById(R.id.btn_fullscreen)
        rotateButton = view.findViewById(R.id.rotate)
        nameFilm = view.findViewById(R.id.nameFilm)

        numberEpInLine = resources.displayMetrics.widthPixels / resources.displayMetrics.densityDpi
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.episodeRecyclerView)
        recyclerView?.layoutManager = GridLayoutManager(requireContext(), ++numberEpInLine)

        observeFilmDetails()
        observeEffect()

        slug?.let { s ->
            viewLifecycleOwner.lifecycleScope.launch {
                historyViewModel.getHistoryItem(s)?.let {
                    currentEpisode = it.episode
                    currentPosition = it.position
                }
                filmDetailsViewModel.getFilmDetails(s)
            }
        }

        startPlaybackMonitor()
        settingExoPlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentEpisode", currentEpisode)
        outState.putBoolean("isFullscreen", isFullscreen)
        exoPlayer?.let { outState.putLong("exoPlayerPosition", it.currentPosition) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            isFullscreen = savedInstanceState.getBoolean("isFullscreen", false)
            toggleFullscreen()
            currentEpisode = savedInstanceState.getInt("currentEpisode", 0)
            currentPosition = (savedInstanceState.getLong("exoPlayerPosition", 0) - 1000).coerceAtLeast(0)
            playEpisode(currentEpisode)
        }
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.apply {
            historyViewModel.addToHistory(slug.toString(), currentEpisode, currentPosition)
            pause()
            btnPlayPause?.isSelected = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun observeFilmDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                filmDetailsViewModel.state.collect { state ->
                    if (state.film != null) {
                        film = state.film
                        playEpisode(currentEpisode)
                        view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.episodeRecyclerView)
                            ?.adapter = EpisodeAdapter(state.film.episodes) { ep ->
                                playEpisode(ep)
                            }
                    }
                }
            }
        }

        playFilmViewModel.loadPremiumStatus()
    }

    private fun observeEffect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                playFilmViewModel.effect.collect { effect ->
                    when (effect) {
                        is PlayFilmUiEffect.ShowPremiumDialog -> {
                            exoPlayer?.pause()
                            btnPlayPause?.isSelected = false
                            exoPlayer?.seekTo((5 * 60 * 1000) - 1000)
                            showPremiumDialog()
                        }
                        is PlayFilmUiEffect.NavigateToPayment -> {
                            findNavController().navigate(R.id.action_play_film_to_payment)
                        }
                        is PlayFilmUiEffect.NavigateToLogin -> {
                            findNavController().navigate(R.id.action_play_film_to_login)
                        }
                    }
                }
            }
        }
    }

    private fun playEpisode(episodeIndex: Int) {
        film?.let { filmDetails ->
            if (episodeIndex in filmDetails.episodes.indices) {
                val episode = filmDetails.episodes[episodeIndex]
                exoPlayer?.setMediaItem(MediaItem.fromUri(episode.linkM3u8))
                exoPlayer?.prepare()
                exoPlayer?.seekTo(currentPosition)
                exoPlayer?.play()
                nameFilm?.text = episode.filename
                if (currentEpisode != episodeIndex) {
                    currentPosition = 0
                }
                currentEpisode = episodeIndex
                btnPlayPause?.isSelected = true
            }
        }
    }

    private fun settingExoPlayer() {
        exoPlayer?.apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        playEpisode(currentEpisode + 1)
                    }
                }
            })

            btnPlayPause?.isSelected = true
            btnPlayPause?.setOnClickListener {
                if (isPlaying) {
                    pause()
                    btnPlayPause?.isSelected = false
                } else {
                    play()
                    btnPlayPause?.isSelected = true
                }
            }

            btnRewind?.setOnClickListener {
                val rewindPosition = currentPosition - 5000
                exoPlayer?.seekTo(rewindPosition.coerceAtLeast(0))
            }
            btnForward?.setOnClickListener {
                val forwardPosition = currentPosition + 15000
                seekTo(forwardPosition.coerceAtMost(duration))
            }
            btnPrevious?.setOnClickListener { playEpisode(currentEpisode - 1) }
            btnNext?.setOnClickListener { playEpisode(currentEpisode + 1) }

            fullscreenButton?.setOnClickListener {
                isFullscreen = !isFullscreen
                toggleFullscreen()
            }

            rotateButton?.setOnClickListener {
                requireActivity().requestedOrientation = if (requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }
    }

    private fun toggleFullscreen() {
        val recyclerView = view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.episodeRecyclerView)
        val otherViews = listOf(recyclerView, nameFilm)
        playerView?.apply {
            if (!isFullscreen) {
                SystemUiController.showSystemUI(requireActivity().window)
                otherViews.forEach { it?.visibility = View.VISIBLE }
                layoutParams.height = dpToPx(250f, resources.displayMetrics).toInt()
            } else {
                SystemUiController.autoHideSystemUI(requireActivity().window)
                otherViews.forEach { it?.visibility = View.GONE }
                layoutParams.height = resources.displayMetrics.heightPixels
            }
        }
    }

    private fun startPlaybackMonitor() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    exoPlayer?.let { player ->
                        playFilmViewModel.checkPlaybackThrottle(
                            position = player.currentPosition,
                            maxPreviewDurationMs = 5L * 60 * 1000,
                        )
                    }
                    delay(1000)
                }
            }
        }
    }

    private fun showPremiumDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm, null)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnConfirm)

        title.text = getString(R.string.watched_more_than_5_minutes)
        btnCreate.text = getString(R.string.upgrade_now)

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnCreate.setOnClickListener {
            dialog.dismiss()
            playFilmViewModel.onUpgradeClicked()
        }

        dialog.show()
    }
}
