package com.drs.auralife.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R

class NotificationAdapter(
    private val notifications: MutableList<Pair<String, String>>,
    private val onItemClicked: (Pair<String, String>) -> Unit,
    private val onIconButtonClicked: (Pair<String, String>) -> Unit,
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    class NotificationViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val tvNotificationItem: TextView = itemView.findViewById(R.id.tvNotificationItem)
        val tvIconButton: ImageButton = itemView.findViewById(R.id.tvIconButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NotificationViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int,
    ) {
        val notification = notifications[position]
        holder.tvNotificationItem.text = notification.second

        holder.tvNotificationItem.setOnClickListener {
            onItemClicked(notification)
        }

        holder.tvIconButton.setOnClickListener {
            removeItem(notification.first)
            onIconButtonClicked(notification)
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun removeItem(slug: String) {
        val index = notifications.indexOfFirst { it.first == slug }
        if (index != -1) {
            notifications.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
