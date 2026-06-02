package com.drs.auralife.feature.library.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.feature.library.R
import com.drs.auralife.designsystem.AuraLifeGlideModule
import com.drs.auralife.domain.model.Library
import com.drs.auralife.feature.library.library.EditLibraryDialog

class LibraryAdapter(
    private val onRename: (oldName: String, newName: String) -> Unit,
    private val onDelete: (name: String) -> Unit,
    private val onItemClick: (name: String) -> Unit,
) : ListAdapter<Library, LibraryAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImage: ImageView = itemView.findViewById(R.id.imageLibrary)
        val tvTitle: TextView = itemView.findViewById(R.id.nameLibrary)
        val tvDetails: TextView = itemView.findViewById(R.id.detailsLibrary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_library, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context

        AuraLifeGlideModule.loadImage(context, item.posterUrl, holder.tvImage)

        holder.tvTitle.text = item.name
        holder.tvDetails.text = buildString {
            append(context.getString(R.string.quantity))
            append(": ")
            append(item.films.size.toString())
        }

        holder.itemView.setOnClickListener { onItemClick(item.name) }

        holder.itemView.setOnLongClickListener {
            EditLibraryDialog.showEditLibraryDialog(
                context,
                item.name,
                onRename = { newName -> onRename(item.name, newName) },
                onDelete = { onDelete(item.name) },
            )
            true
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Library>() {
        override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem == newItem
    }
}
