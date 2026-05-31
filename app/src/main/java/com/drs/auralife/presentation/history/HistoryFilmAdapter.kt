package com.drs.auralife.presentation.history

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.domain.model.Film
import com.drs.auralife.presentation.filmdetails.FilmDetailsActivity
import com.drs.auralife.presentation.filmdetails.EXTRA_SLUG
import com.drs.auralife.core.utils.MyAppGlideModule

class HistoryFilmAdapter : RecyclerView.Adapter<HistoryFilmAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem.slug == newItem.slug
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem == newItem
    })

    interface Listener {
        fun onLongClick(slug: String)
    }

    private var listener: Listener? = null

    fun setCallback(listener: Listener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.posterView)
        val tvTitle: TextView = itemView.findViewById(R.id.nameFilm)
        val tvDetails: TextView = itemView.findViewById(R.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = differ.currentList[position]
        holder.apply {
            tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvDetails.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvTitle.text = film.title
            tvDetails.text = film.description
            MyAppGlideModule.loadImage(itemView.context, film.posterUrl, ivPoster)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, FilmDetailsActivity::class.java)
                intent.putExtra(EXTRA_SLUG, film.slug)
                itemView.context.startActivity(intent)
            }
            itemView.setOnLongClickListener {
                listener?.onLongClick(film.slug)
                true
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun replaceItems(newItems: List<Film>) {
        differ.submitList(newItems)
    }

    fun clearItems() {
        differ.submitList(emptyList())
    }
}
