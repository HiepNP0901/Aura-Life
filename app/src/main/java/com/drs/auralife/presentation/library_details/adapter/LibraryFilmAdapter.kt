package com.drs.auralife.presentation.library_details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.core.utils.MyAppGlideModule
import com.drs.auralife.domain.model.Film

class LibraryFilmAdapter(
    private val onItemClick: (String) -> Unit,
    private val onLongClick: (String) -> Unit,
) : ListAdapter<Film, LibraryFilmAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.posterView)
        val tvTitle: TextView = itemView.findViewById(R.id.nameFilm)
        val tvDetails: TextView = itemView.findViewById(R.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = getItem(position)
        holder.apply {
            tvTitle.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            tvDetails.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            tvTitle.text = film.title
            tvDetails.text = film.description
            MyAppGlideModule.loadImage(itemView.context, film.posterUrl, ivPoster)
            itemView.setOnClickListener { onItemClick(film.slug) }
            itemView.setOnLongClickListener {
                onLongClick(film.slug)
                true
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem.slug == newItem.slug
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem == newItem
    }
}
