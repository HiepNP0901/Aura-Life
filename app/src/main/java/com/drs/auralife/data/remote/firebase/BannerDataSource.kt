package com.drs.auralife.data.remote.firebase

import com.google.firebase.database.FirebaseDatabase

object BannerDataSource {
    val bannerRef = FirebaseDatabase.getInstance().getReference("banners")

    fun getBannerData(onDataReceived: (List<Pair<String, String>>) -> Unit) {
        bannerRef.get().addOnSuccessListener { data ->
            val bannerList = mutableListOf<Pair<String, String>>()
            for (snapshot in data.children) {
                val bannerData = snapshot.getValue(String::class.java)
                bannerData?.let { bannerList.add(Pair(snapshot.key.toString(), it)) }
            }
            onDataReceived(bannerList)
        }
    }
}
