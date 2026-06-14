package com.drs.auralife.core.network.model

data class Films(
    val items: List<Movie>,
    val pagination: Pagination,
    val status: Boolean,
) {
    data class Pagination(
        val currentPage: Int,
        val totalItems: Int,
        val totalItemsPerPage: Int,
        val totalPages: Int,
    )
}
