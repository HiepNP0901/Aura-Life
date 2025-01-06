package com.drs.auralife.data.model.films

data class Films(
    val items: List<Item>,
    val pagination: Pagination,
    val status: Boolean
)