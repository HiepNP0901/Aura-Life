package com.drs.auralife.core.firebase.model.history

data class History(
    val slug: String,
    val episode: Int,
    val position: Long,
    var date: String,
)
