package com.drs.auralife.data.remote.firebase

import com.drs.auralife.data.remote.firebase.model.category.Category
import com.google.firebase.database.FirebaseDatabase

object CategoryDataSource {
    val categoryRef = FirebaseDatabase.getInstance().getReference("categories")

    fun getCategoryData(onDataReceived: (List<Category>) -> Unit) {
        categoryRef
            .get()
            .addOnSuccessListener {
                val categoryList = mutableListOf<Category>()
                for (snapshot in it.children) {
                    val categoryData = Category(
                        snapshot.key.toString(),
                        snapshot.child("en").value.toString(),
                        snapshot.child("vi").value.toString(),
                    )
                    categoryData.let { categoryList.add(it) }
                }
                onDataReceived(categoryList)
            }.addOnFailureListener {
                onDataReceived(emptyList())
            }
    }
}
