package com.drs.auralife.data.firebase.realtime.database

import com.google.firebase.database.FirebaseDatabase

object BannerRepository {
    val bannerRef = FirebaseDatabase.getInstance().getReference("banners")

    fun getBannerData(onDataReceived: (List<Pair<String, String>>) -> Unit) {
        bannerRef.get().addOnSuccessListener {
            val bannerList = mutableListOf<Pair<String, String>>()
            for (snapshot in it.children) {
                val bannerData = snapshot.getValue(String::class.java)
                bannerData?.let { bannerList.add(Pair(snapshot.key.toString(), it)) }
            }
            onDataReceived(bannerList)
        }
    }
}
