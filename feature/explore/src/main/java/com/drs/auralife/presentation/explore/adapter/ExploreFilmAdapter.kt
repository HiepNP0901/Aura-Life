package com.drs.auralife.presentation.explore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.core.designsystem.R as DsR
import com.drs.auralife.designsystem.AuraLifeGlideModule
import com.drs.auralife.domain.model.Film

class ExploreFilmAdapter(
    private val onItemClick: (String) -> Unit,
) : RecyclerView.Adapter<ExploreFilmAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem.slug == newItem.slug
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem == newItem
    })

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(DsR.id.posterView)
        val tvTitle: TextView = itemView.findViewById(DsR.id.nameFilm)
        val tvDetails: TextView = itemView.findViewById(DsR.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(DsR.layout.item_film_vertical, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = differ.currentList[position]
        holder.apply {
            tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvDetails.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvTitle.text = film.title
            AuraLifeGlideModule.loadImage(itemView.context, film.posterUrl, ivPoster)
            itemView.setOnClickListener { onItemClick(film.slug) }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun addItem(newItems: List<Film>) {
        val current = differ.currentList.toMutableList()
        newItems.forEach { film ->
            val existingIndex = current.indexOfFirst { it.slug == film.slug }
            if (existingIndex != -1) {
                current[existingIndex] = film
            } else {
                current.add(film)
            }
        }
        differ.submitList(current)
    }
}
