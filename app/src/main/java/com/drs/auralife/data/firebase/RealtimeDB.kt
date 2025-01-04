package com.drs.auralife.data.firebase

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.drs.auralife.utils.ImageEncoderDecoder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.drs.auralife.R

class RealtimeDB {
    companion object{
        private val database = FirebaseDatabase.getInstance()

        private val bannerRef = database.getReference("banners")

        private val userRef = database.getReference("users")


        fun uploadAvatar(context: Context, bitmap: Bitmap) {
            val base64String = ImageEncoderDecoder.encodeToBase64(bitmap)
            val userId = Authentication.getUserId()

            userId.let {
                userRef.child(it.toString()).child("avatar").setValue(base64String)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            context.getString(R.string.upload_avatar_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { _ ->
                        Toast.makeText(
                            context,
                            context.getString(R.string.upload_avatar_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }


        fun getAvatar(callback: (Bitmap) -> Unit) {
            val userId = Authentication.getUserId()

            userId.let {
                userRef.child(it.toString()).child("avatar").get().addOnSuccessListener {
                    ImageEncoderDecoder
                        .decodeFromBase64(it.value.toString())
                        ?.let { bitmap ->
                            callback(bitmap)
                        }
                }
            }
        }


        fun getBannerData(onDataReceived: (List<Pair<String, String>>) -> Unit) {

            bannerRef.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val bannerPair = mutableListOf<Pair<String, String>>()

                    for (snapshot in dataSnapshot.children) {

                        val key = snapshot.key

                        val value = snapshot.getValue(String::class.java)

                        bannerPair.add(Pair(key ?: "", value ?: ""))
                    }

                    onDataReceived(bannerPair)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onDataReceived(emptyList())
                }
            })
        }


        fun getLibraryData(onDataReceived: (List<Pair<String, String>>) -> Unit) {
            val userId = Authentication.getUserId()

            userId.let {
                userRef.child(it.toString()).child("library").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val libraryPair = mutableListOf<Pair<String, String>>()
                        for (snapshot in dataSnapshot.children) {
                            val key = snapshot.key
                            val value = snapshot.getValue(String::class.java)
                            libraryPair.add(Pair(key ?: "", value ?: ""))
                        }
                        onDataReceived(libraryPair)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        onDataReceived(emptyList())
                    }
                })
            }
        }


        fun addLibraryData(key: String, value: String) {
            val userId = Authentication.getUserId()
            userId.let {
                userRef.child(it.toString()).child("library").child(key).setValue(value)
            }
        }

        fun removeLibraryData(key: String) {
            val userId = Authentication.getUserId()
            userId.let {
                userRef.child(it.toString()).child("library").child(key).removeValue()
            }
        }
    }
}