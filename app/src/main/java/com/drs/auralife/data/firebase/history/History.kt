package com.drs.auralife.data.firebase.history

data class History(
    val slug: String,
    val episode: Int,
    val position: Long,
    var date: String
)