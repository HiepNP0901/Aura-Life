package com.drs.auralife.ui.film

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.drs.auralife.R
import com.drs.auralife.data.model.films.FilmPreviews

class FilmAdapter(private val films: MutableList<FilmPreviews>) : RecyclerView.Adapter<FilmAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImage = itemView.findViewById<ImageView>(R.id.imageFilm)
        val tvTitle = itemView.findViewById<TextView>(R.id.nameFilm)
        val tvEpisode = itemView.findViewById<TextView>(R.id.currentEpisode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val film = films[position]

        Glide.with(holder.tvImage.context)
            .load(film.thumbUrl)
            .transform(RoundedCorners(16))
            .placeholder(R.drawable.bg_logo)
            .error(R.drawable.rounded)
            .into(holder.tvImage)

        holder.tvTitle.text = film.name
        holder.tvEpisode.text = film.currentEpisode
    }

    override fun getItemCount(): Int = films.size

    fun addItem(newItem: List<FilmPreviews>) {
        films.addAll(newItem)
        notifyItemInserted(films.size - 1)
    }
}
