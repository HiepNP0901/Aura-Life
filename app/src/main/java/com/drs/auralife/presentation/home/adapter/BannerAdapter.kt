package com.drs.auralife.presentation.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.core.utils.MyAppGlideModule

class BannerAdapter(
    private val banners: List<Pair<String, String>>,
    private val onBannerClick: (String) -> Unit,
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerImage: ImageView = itemView.findViewById(R.id.bannerImage)

        fun bind(banner: Pair<String, String>) {
            MyAppGlideModule.loadImage(bannerImage.context, banner.second, bannerImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position])
        holder.itemView.setOnClickListener {
            onBannerClick(banners[position].first)
        }
    }

    override fun getItemCount(): Int = banners.size
}
