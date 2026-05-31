package com.drs.auralife.data.remote.api.model.films

data class Pagination(
    val currentPage: Int,
    val totalItems: Int,
    val totalItemsPerPage: Int,
    val totalPages: Int,
)
