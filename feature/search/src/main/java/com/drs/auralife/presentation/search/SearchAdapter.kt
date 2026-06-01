package com.drs.auralife.presentation.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.feature.search.R
import com.drs.auralife.domain.model.Film
import com.drs.auralife.presentation.common.AuraLifeGlideModule

class SearchAdapter(
    private val onItemClick: (String) -> Unit,
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem.slug == newItem.slug
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem == newItem
    })

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
        val film = differ.currentList[position]
        holder.tvTitle.text = film.title
        holder.tvDetails.text = film.description
        AuraLifeGlideModule.loadImage(
            holder.itemView.context,
            film.posterUrl,
            holder.posterView,
        )
        holder.itemView.setOnClickListener {
            onItemClick(film.slug)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun replaceItems(newItems: List<Film>) {
        differ.submitList(newItems)
    }
}
