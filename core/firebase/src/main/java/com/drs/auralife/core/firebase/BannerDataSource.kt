package com.drs.auralife.core.firebase

import com.drs.auralife.core.firebase.model.Banner
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class BannerDataSource @Inject constructor(
    database: FirebaseDatabase,
) {
    private val bannerRef = database.getReference("banners")

    fun getBannerData(onDataReceived: (List<Banner>) -> Unit) {
        bannerRef.get().addOnSuccessListener { data ->
            val bannerList = mutableListOf<Banner>()
            for (snapshot in data.children) {
                val bannerData = snapshot.getValue(String::class.java)
                bannerData?.let {
                    bannerList.add(Banner(imageUrl = it, filmSlug = snapshot.key.toString()))
                }
            }
            onDataReceived(bannerList)
        }
    }
}
