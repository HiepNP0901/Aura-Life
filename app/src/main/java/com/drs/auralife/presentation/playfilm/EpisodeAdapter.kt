package com.drs.auralife.presentation.playfilm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.domain.model.FilmEpisode

class EpisodeAdapter(
    private val episodes: List<FilmEpisode>,
    private val onItemClick: (Int) -> Unit,
) : RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {
    inner class ViewHolder(
        view: View,
    ) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.episodeNumber)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_episode, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.textView.text = episodes[position].name

        holder.textView.setOnClickListener {
            onItemClick(position)
            holder.itemView.isSelected = true
        }
    }

    override fun getItemCount(): Int = episodes.size
}
