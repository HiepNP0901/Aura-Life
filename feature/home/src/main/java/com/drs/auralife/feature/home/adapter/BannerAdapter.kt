package com.drs.auralife.feature.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.feature.home.R
import com.drs.auralife.domain.model.Banner
import com.drs.auralife.designsystem.AuraLifeGlideModule

class BannerAdapter(
    private val banners: List<Banner>,
    private val onBannerClick: (String) -> Unit,
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerImage: ImageView = itemView.findViewById(R.id.bannerImage)

        fun bind(banner: Banner) {
            AuraLifeGlideModule.loadImage(bannerImage.context, banner.imageUrl, bannerImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position])
        holder.itemView.setOnClickListener {
            onBannerClick(banners[position].filmSlug)
        }
    }

    override fun getItemCount(): Int = banners.size
}
