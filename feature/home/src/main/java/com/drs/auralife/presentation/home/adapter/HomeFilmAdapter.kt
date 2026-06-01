package com.drs.auralife.presentation.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.feature.home.R
import com.drs.auralife.core.util.TimeUtils
import com.drs.auralife.presentation.common.AuraLifeGlideModule
import com.drs.auralife.domain.model.Film
import java.time.Instant

class HomeFilmAdapter(
    private val onItemClick: (String) -> Unit,
) : ListAdapter<Film, HomeFilmAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.posterView)
        val tvTitle: TextView = itemView.findViewById(R.id.nameFilm)
        val tvDetails: TextView = itemView.findViewById(R.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_vertical, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = getItem(position)
        holder.apply {
            tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvDetails.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvTitle.text = film.title
            if (film.modifiedAt > 0) {
                tvDetails.text = TimeUtils.calculateTimeDifference(Instant.ofEpochMilli(film.modifiedAt), itemView.context)
            }
            AuraLifeGlideModule.loadImage(itemView.context, film.posterUrl, ivPoster)
            itemView.setOnClickListener { onItemClick(film.slug) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem.slug == newItem.slug
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem == newItem
    }
}
