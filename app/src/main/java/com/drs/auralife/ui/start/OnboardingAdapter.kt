package com.drs.auralife.ui.start

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.data.model.OnboardingItem
import com.drs.auralife.R

class OnboardingAdapter(private val onboardingItems: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageSlide = view.findViewById<ImageView>(R.id.onboardingLogo)
        private val textTitle = view.findViewById<TextView>(R.id.onboardingText)

        fun bind(onboardingItem: OnboardingItem) {
            imageSlide.setImageResource(onboardingItem.image)
            textTitle.text = onboardingItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun getItemCount(): Int = onboardingItems.size

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }
}
