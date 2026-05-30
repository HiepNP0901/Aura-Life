package com.drs.auralife.presentation.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.domain.model.Film
import com.drs.auralife.presentation.filmdetails.FilmDetailsActivity
import com.drs.auralife.presentation.filmdetails.EXTRA_SLUG
import com.drs.auralife.core.utils.MyAppGlideModule

class SearchFilmAdapter(
    private val filmList: MutableList<Film>,
) : RecyclerView.Adapter<SearchFilmAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val posterView: ImageView = itemView.findViewById(R.id.posterView)
        val tvTitle: TextView = itemView.findViewById(R.id.nameFilm)
        val tvDetails: TextView = itemView.findViewById(R.id.details)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_film_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val film = filmList[position]
        holder.tvTitle.text = film.title
        holder.tvDetails.text = film.description
        MyAppGlideModule.loadImage(
            holder.itemView.context,
            film.posterUrl,
            holder.posterView,
        )
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, FilmDetailsActivity::class.java)
            intent.putExtra(EXTRA_SLUG, film.slug)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filmList.size

    fun replaceItems(newItems: List<Film>) {
        filmList.clear()
        filmList.addAll(newItems)
        notifyDataSetChanged()
    }
}
