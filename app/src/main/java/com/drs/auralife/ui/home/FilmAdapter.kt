package com.drs.auralife.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.data.model.films.Item
import com.drs.auralife.ui.film.details.FilmDetailsActivity
import com.drs.auralife.utils.MyAppGlideModule
import java.time.Duration
import java.time.Instant

const val SLUG = "@slug"

class FilmAdapter(private val films: MutableList<Item>, private val numberFilmInLine: Int): RecyclerView.Adapter<FilmAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImage = itemView.findViewById<ImageView>(R.id.posterView)
        val tvTitle = itemView.findViewById<TextView>(R.id.nameFilm)
        val tvDetails = itemView.findViewById<TextView>(R.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        view.layoutParams.width = (parent.width-15*numberFilmInLine)/numberFilmInLine
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val film = films[position]

        MyAppGlideModule.loadImage(holder.tvImage.context, film.thumbUrl, holder.tvImage)

        holder.tvTitle.text = film.name

        holder.tvDetails.text = film.modified.time.let{calculateTimeDifference(it)}

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, FilmDetailsActivity::class.java)
            intent.putExtra(SLUG, film.slug)
            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.isSelected = true
    }

    override fun getItemCount(): Int = films.size

    fun addItem(newItems: List<Item>) {
        films.addAll(newItems)
        notifyItemInserted(films.size - 1)
    }

    private fun calculateTimeDifference(updateTime: String): String {
        val updateInstant = Instant.parse(updateTime) - Duration.ofHours(6)
        val currentInstant = Instant.now()

        val duration = Duration.between(updateInstant, currentInstant)
        val days = duration.toDays()
        val hours = duration.toHours()
        val minutes = duration.toMinutes()

        return when {
            days > 0 -> "$days ngày trước"
            hours > 0 -> "${hours % 24} giờ trước"
            minutes > 0 -> "${minutes % 60} phút trước"
            else -> "Vừa xong"
        }
    }
}
