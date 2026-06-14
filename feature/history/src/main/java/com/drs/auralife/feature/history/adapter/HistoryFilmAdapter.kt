package com.drs.auralife.feature.history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.core.common.util.TimeUtils
import com.drs.auralife.designsystem.AuraLifeGlideModule
import com.drs.auralife.feature.history.model.HistoryFilm
import java.time.Instant
import com.drs.auralife.core.designsystem.R as DsR

class HistoryFilmAdapter(
    private val onItemClick: (String) -> Unit,
    private val onLongClick: (String) -> Unit,
) : ListAdapter<HistoryFilm, HistoryFilmAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(DsR.id.posterView)
        val tvTitle: TextView = itemView.findViewById(DsR.id.nameFilm)
        val tvDetails: TextView = itemView.findViewById(DsR.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(DsR.layout.item_film_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = getItem(position)
        holder.apply {
            tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvDetails.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvTitle.text = film.title
            tvDetails.text = if (film.watchedAt > 0) {
                TimeUtils.calculateTimeDifference(Instant.ofEpochMilli(film.watchedAt), itemView.context)
            } else {
                film.description
            }
            AuraLifeGlideModule.loadImage(itemView.context, film.posterUrl, ivPoster)
            itemView.setOnClickListener { onItemClick(film.slug) }
            itemView.setOnLongClickListener {
                onLongClick(film.slug)
                true
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    private object DiffCallback : DiffUtil.ItemCallback<HistoryFilm>() {
        override fun areItemsTheSame(oldItem: HistoryFilm, newItem: HistoryFilm): Boolean = oldItem.slug == newItem.slug
        override fun areContentsTheSame(oldItem: HistoryFilm, newItem: HistoryFilm): Boolean = oldItem == newItem
    }
}
