package com.drs.auralife.ui.home

import android.annotation.SuppressLint
import android.content.Context
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
import java.util.Locale

const val SLUG = "@slug"
const val GRID = 1
const val LINEAR = 2

@SuppressLint("NotifyDataSetChanged")
class FilmAdapter(private val films: MutableList<Item>, private val itemViewType: Int = GRID): RecyclerView.Adapter<FilmAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImage = itemView.findViewById<ImageView>(R.id.posterView)
        val tvTitle = itemView.findViewById<TextView>(R.id.nameFilm)
        val tvDetails = itemView.findViewById<TextView>(R.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = when (itemViewType) {
            LINEAR -> LayoutInflater.from(parent.context).inflate(R.layout.item_film_linear, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.item_film_grid, parent, false)
        }
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val film = films[position]
        val context = holder.itemView.context

        MyAppGlideModule.loadImage(context, film.posterUrl, holder.tvImage)

        if(Locale.getDefault().language == "vi") {
            holder.tvTitle.text = film.name
        } else {
            holder.tvTitle.text = film.originName
        }

        holder.tvDetails.text = film.modified.time.let{calculateTimeDifference(it, context)}

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FilmDetailsActivity::class.java)
            intent.putExtra(SLUG, film.slug)
            context.startActivity(intent)
        }

        holder.itemView.isSelected = true
    }

    override fun getItemCount(): Int = films.size

    fun addItem(newItems: List<Item>) {
        films.addAll(newItems)
        notifyItemInserted(films.size)
    }

    fun replaceItems(newItems: List<Item>) {
        films.clear()
        films.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clearItems() {
        films.clear()
        notifyDataSetChanged()
    }

    private fun calculateTimeDifference(updateTime: String, context: Context): String {
        val updateInstant = Instant.parse(updateTime) - Duration.ofHours(7)
        val currentInstant = Instant.now()

        val duration = Duration.between(updateInstant, currentInstant)
        val days = duration.toDays()
        val hours = duration.toHours()
        val minutes = duration.toMinutes()

        return when {
            days > 0 -> days.toString() + context.getString(R.string.days_ago)
            hours > 0 -> (hours % 24).toString() + context.getString(R.string.hours_ago)
            minutes > 0 -> (minutes % 60).toString() + context.getString(R.string.minutes_ago)
            else -> context.getString(R.string.just_finished)
        }
    }
}
