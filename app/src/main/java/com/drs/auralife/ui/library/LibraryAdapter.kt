package com.drs.auralife.ui.library

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.data.firebase.library.Library
import com.drs.auralife.utils.MyAppGlideModule
import com.drs.auralife.R

class LibraryAdapter(private val library: MutableList<Library>, private val fragment: LibraryFragment): RecyclerView.Adapter<LibraryAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImage = itemView.findViewById<ImageView>(R.id.imageLibrary)
        val tvTitle = itemView.findViewById<TextView>(R.id.nameLibrary)
        val tvDetails = itemView.findViewById<TextView>(R.id.detailsLibrary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_library, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = library[position]
        val context = holder.itemView.context

        MyAppGlideModule.loadImage(context, item.posterUrl, holder.tvImage)

        holder.tvTitle.text = item.name

        holder.tvDetails.text = context.getString(R.string.quantity) +": " + item.listFilm.size.toString()

        holder.itemView.setOnClickListener{
            val intent = Intent(context, LibraryDetailsActivity::class.java)
            intent.putExtra(LIBRARY_NAME, item.name)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener{
            EditLibrary.showEditLibraryDialog(context, item.name){
                fragment.refreshLibrary()
            }
            true
        }
    }

    override fun getItemCount() = library.size

    @SuppressLint("NotifyDataSetChanged")
    fun refreshLibrary(newLibrary: MutableList<Library>) {
        library.clear()
        library.addAll(newLibrary)
        library.sortBy { it.name }
        notifyDataSetChanged()
    }
}