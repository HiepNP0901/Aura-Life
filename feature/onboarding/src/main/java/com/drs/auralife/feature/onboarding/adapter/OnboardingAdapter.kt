package com.drs.auralife.feature.onboarding.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.feature.onboarding.R
import com.drs.auralife.domain.model.OnboardingItem

class OnboardingAdapter(
    private val items: List<OnboardingItem>,
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    class OnboardingViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.onboardingLogo)
        val textView: TextView = itemView.findViewById(R.id.onboardingText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): OnboardingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: OnboardingViewHolder,
        position: Int,
    ) {
        holder.imageView.setImageResource(items[position].image)
        holder.textView.text = items[position].title
    }
}
