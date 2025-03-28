package com.drs.auralife.ui.film

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.ui.film.details.FilmDetailsActivity
import com.drs.auralife.utils.MyAppGlideModule
import com.drs.auralife.utils.Time
import java.time.Duration
import java.time.Instant
import java.util.Locale

const val SLUG = "@slug"
const val VERTICAL = 1
const val HORIZONTAL = 2

open class FilmAdapter(
    private val films: MutableList<Movie>,
    private val itemViewType: Int = VERTICAL,
    private val centerTitle: Boolean = true
) : RecyclerView.Adapter<FilmAdapter.ItemViewHolder>() {

    interface FragmentListener {
        fun onLongClick(slug: String)
    }

    private var callback: FragmentListener? = null

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImage = itemView.findViewById<ImageView>(R.id.posterView)!!
        val tvTitle = itemView.findViewById<TextView>(R.id.nameFilm)!!
        val tvDetails = itemView.findViewById<TextView>(R.id.details)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = when (itemViewType) {
            HORIZONTAL -> LayoutInflater.from(parent.context).inflate(R.layout.item_film_horizontal, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.item_film_vertical, parent, false)
        }
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val film = films[position]
        val context = holder.itemView.context

        if (centerTitle) {
            holder.tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
            holder.tvDetails.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        else {
            holder.tvTitle.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            holder.tvDetails.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }

        if(Locale.getDefault().language == "vi") {
            holder.tvTitle.text = film.name
        } else {
            holder.tvTitle.text = film.originName
        }

        MyAppGlideModule.loadImage(context, film.posterUrl, holder.tvImage)

        @Suppress("SENSELESS_COMPARISON")
        if (itemViewType == VERTICAL) {
            holder.tvDetails.text = film.modified.time.let {
                Time.calculateTimeDifference(
                    Instant.parse(it) - Duration.ofHours(7), context
                )
            }
        }
        else if (film.content != null) {
            holder.tvDetails.text =
                HtmlCompat.fromHtml(film.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        else {
            holder.tvTitle.isSingleLine = false
            holder.tvTitle.maxLines = 4
            holder.tvDetails.text = film.modified.time.let {
                Time.calculateTimeDifference(
                    Instant.parse(it) - Duration.ofHours(7), context
                )
            }
            holder.tvDetails.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FilmDetailsActivity::class.java)
            intent.putExtra(SLUG, film.slug)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            callback?.onLongClick(film.slug)
            true
        }

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            holder.itemView.isSelected = true
        }, 3000)
    }

    override fun getItemCount(): Int = films.size

    fun addItem(newItems: List<Movie>) {
        newItems.forEach { movie ->
            if (films.map { it.slug }.contains(movie.slug)) {
                val position = films.indexOfFirst { it.slug == movie.slug }
                films[position] = movie
            }
            else {
                films.add(movie)
            }
        }
        notifyItemInserted(films.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceItems(newItems: List<Movie>) {
        films.clear()
        films.addAll(newItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItems() {
        films.clear()
        notifyDataSetChanged()
    }

    fun removeItem(slug: String) {
        val position = films.indexOfFirst { it.slug == slug }
        if (position != -1) {
            films.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun setCallback(context: FragmentListener) {
        this.callback = context
    }
}
