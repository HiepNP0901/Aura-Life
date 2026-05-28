package com.drs.auralife.presentation.film

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.domain.model.Film
import com.drs.auralife.presentation.film.details.FilmDetailsActivity
import com.drs.auralife.core.utils.MyAppGlideModule

const val SLUG = "@slug"
const val VERTICAL = 1
const val HORIZONTAL = 2

open class FilmAdapter(
    private val films: MutableList<Film>,
    private val itemViewType: Int = VERTICAL,
    private val centerTitle: Boolean = true,
) : RecyclerView.Adapter<FilmAdapter.ItemViewHolder>() {
    interface FragmentListener {
        fun onLongClick(slug: String)
    }

    private var callback: FragmentListener? = null

    class ItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val tvImage: ImageView = itemView.findViewById<ImageView>(R.id.posterView)
        val tvTitle: TextView = itemView.findViewById<TextView>(R.id.nameFilm)
        val tvDetails: TextView = itemView.findViewById<TextView>(R.id.details)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder {
        val view = when (itemViewType) {
            HORIZONTAL ->
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_film_horizontal, parent, false)

            else ->
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_film_vertical, parent, false)
        }
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
    ) {
        holder.apply {
            val film = films[position]
            val context = itemView.context

            if (centerTitle) {
                tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
                tvDetails.textAlignment = View.TEXT_ALIGNMENT_CENTER
            } else {
                tvTitle.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                tvDetails.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            }

            tvTitle.text = film.title

            MyAppGlideModule.loadImage(context, film.posterUrl, tvImage)

            if (itemViewType == VERTICAL) {
                tvDetails.text = film.category
            } else {
                tvDetails.text = film.description
            }

            itemView.setOnClickListener {
                val intent = Intent(context, FilmDetailsActivity::class.java)
                intent.putExtra(SLUG, film.slug)
                context.startActivity(intent)
            }

            itemView.setOnLongClickListener {
                callback?.onLongClick(film.slug)
                true
            }
        }
    }

    override fun getItemCount(): Int = films.size

    fun addItem(newItems: List<Film>) {
        newItems.forEach { film ->
            if (films.map { it.slug }.contains(film.slug)) {
                val position = films.indexOfFirst { it.slug == film.slug }
                films[position] = film
            } else {
                films.add(film)
            }
        }
        notifyItemInserted(films.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceItems(newItems: List<Film>) {
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
