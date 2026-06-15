package com.drs.auralife.feature.film_player

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
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.core.navigation.AppNavigator
import com.drs.auralife.designsystem.SystemUiController
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import com.drs.auralife.feature.film.player.R
import com.drs.auralife.feature.film_detail.FilmDetailsViewModel
import com.drs.auralife.feature.film_player.adapter.EpisodeAdapter
import com.drs.auralife.feature.history.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.drs.auralife.core.designsystem.R as DsR

@AndroidEntryPoint
class FilmPlayerFragment : Fragment() {

    private val appNavigator by lazy { AppNavigator(findNavController()) }

    private val filmDetailsViewModel: FilmDetailsViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val filmPlayerViewModel: FilmPlayerViewModel by viewModels()

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_film_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        nameFilm = view.findViewById(DsR.id.nameFilm)

        numberEpInLine = resources.displayMetrics.widthPixels / resources.displayMetrics.densityDpi
        val recyclerView = view.findViewById<RecyclerView>(R.id.episodeRecyclerView)
        recyclerView?.layoutManager = GridLayoutManager(requireContext(), ++numberEpInLine)

        observeFilmDetails()
        observeState()
        observeEffect()

        val slug = filmPlayerViewModel.slug
        viewLifecycleOwner.lifecycleScope.launch {
            historyViewModel.getHistoryItem(slug)?.let {
                filmPlayerViewModel.restoreState(it.episode, it.position, false)
            }
            filmDetailsViewModel.getFilmDetails(slug)
        }

        startPlaybackMonitor()
        settingExoPlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val state = filmPlayerViewModel.state.value
        outState.putInt("currentEpisode", state.currentEpisode)
        outState.putBoolean("isFullscreen", state.isFullscreen)
        exoPlayer?.let { outState.putLong("exoPlayerPosition", it.currentPosition) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val episode = savedInstanceState.getInt("currentEpisode", 0)
            val position = (savedInstanceState.getLong("exoPlayerPosition", 0) - 1000).coerceAtLeast(0)
            val fullscreen = savedInstanceState.getBoolean("isFullscreen", false)
            filmPlayerViewModel.restoreState(episode, position, fullscreen)
            playEpisode(episode)
        }
    }

    override fun onStop() {
        super.onStop()
        val state = filmPlayerViewModel.state.value
        exoPlayer?.apply {
            historyViewModel.addToHistory(state.slug, state.currentEpisode, state.currentPosition)
            pause()
            btnPlayPause?.isSelected = false
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.let { filmPlayerViewModel.setCurrentPosition(it.currentPosition) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun observeFilmDetails() {
        launchAndRepeatWithViewLifecycle {
            filmDetailsViewModel.state.collect { state ->
                if (state.film != null) {
                    val ep = filmPlayerViewModel.state.value.currentEpisode
                    playEpisode(ep)
                    view?.findViewById<RecyclerView>(R.id.episodeRecyclerView)
                        ?.adapter = EpisodeAdapter(state.film.episodes) { ep ->
                        playEpisode(ep)
                    }
                }
            }
        }

        filmPlayerViewModel.loadPremiumStatus()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                filmPlayerViewModel.state.collect { state ->
                    if (state.isFullscreen) {
                        enterFullscreen()
                    } else {
                        exitFullscreen()
                    }
                }
            }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
            filmPlayerViewModel.effect.collect { effect ->
                when (effect) {
                    is PlayFilmUiEffect.ShowPremiumDialog -> {
                        exoPlayer?.pause()
                        btnPlayPause?.isSelected = false
                        exoPlayer?.seekTo((5 * 60 * 1000) - 1000)
                        showPremiumDialog()
                    }

                    is PlayFilmUiEffect.NavigateToPayment -> {
                        appNavigator.navigateToPayment()
                    }

                    is PlayFilmUiEffect.NavigateToLogin -> {
                        appNavigator.navigateToLogin()
                    }
                }
            }
        }
    }

    private fun playEpisode(episodeIndex: Int) {
        val state = filmDetailsViewModel.state.value
        val film = state.film ?: return
        if (episodeIndex in film.episodes.indices) {
            val episode = film.episodes[episodeIndex]
            exoPlayer?.setMediaItem(MediaItem.fromUri(episode.linkM3u8))
            exoPlayer?.prepare()
            val playerState = filmPlayerViewModel.state.value
            exoPlayer?.seekTo(playerState.currentPosition)
            exoPlayer?.play()
            nameFilm?.text = episode.filename
            if (filmPlayerViewModel.state.value.currentEpisode != episodeIndex) {
                filmPlayerViewModel.setCurrentPosition(0)
            }
            filmPlayerViewModel.setCurrentEpisode(episodeIndex)
            btnPlayPause?.isSelected = true
        }
    }

    private fun settingExoPlayer() {
        exoPlayer?.apply {
            addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            playEpisode(filmPlayerViewModel.state.value.currentEpisode + 1)
                        }
                    }
                },
            )

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
                val rewindPosition = filmPlayerViewModel.state.value.currentPosition - 5000
                exoPlayer?.seekTo(rewindPosition.coerceAtLeast(0))
            }
            btnForward?.setOnClickListener {
                val forwardPosition = filmPlayerViewModel.state.value.currentPosition + 15000
                seekTo(forwardPosition.coerceAtMost(duration))
            }
            btnPrevious?.setOnClickListener { playEpisode(filmPlayerViewModel.state.value.currentEpisode - 1) }
            btnNext?.setOnClickListener { playEpisode(filmPlayerViewModel.state.value.currentEpisode + 1) }

            fullscreenButton?.setOnClickListener {
                filmPlayerViewModel.toggleFullscreen()
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
        val fullscreen = filmPlayerViewModel.state.value.isFullscreen
        if (fullscreen) enterFullscreen() else exitFullscreen()
    }

    private fun enterFullscreen() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.episodeRecyclerView)
        val otherViews = listOf(recyclerView, nameFilm)
        playerView?.apply {
            SystemUiController.autoHideSystemUI(requireActivity().window)
            otherViews.forEach { it?.visibility = View.GONE }
            layoutParams.height = resources.displayMetrics.heightPixels
        }
    }

    private fun exitFullscreen() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.episodeRecyclerView)
        val otherViews = listOf(recyclerView, nameFilm)
        playerView?.apply {
            SystemUiController.showSystemUI(requireActivity().window)
            otherViews.forEach { it?.visibility = View.VISIBLE }
            layoutParams.height = dpToPx(250f, resources.displayMetrics).toInt()
        }
    }

    private fun startPlaybackMonitor() {
        launchAndRepeatWithViewLifecycle {
            while (true) {
                exoPlayer?.let { player ->
                    filmPlayerViewModel.checkPlaybackThrottle(
                        position = player.currentPosition,
                        maxPreviewDurationMs = 5L * 60 * 1000,
                    )
                }
                delay(1000)
            }
        }
    }

    private fun showPremiumDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(DsR.layout.dialog_confirm, null)
        val title = dialogView.findViewById<TextView>(DsR.id.title)
        val btnCancel = dialogView.findViewById<Button>(DsR.id.btnCancel)
        val btnCreate = dialogView.findViewById<Button>(DsR.id.btnConfirm)

        title.text = getString(R.string.watched_more_than_5_minutes)
        btnCreate.text = getString(R.string.upgrade_now)

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnCreate.setOnClickListener {
            dialog.dismiss()
            filmPlayerViewModel.onUpgradeClicked()
        }

        dialog.show()
    }
}
