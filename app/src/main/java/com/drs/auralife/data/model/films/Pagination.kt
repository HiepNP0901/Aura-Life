package com.drs.auralife.data.model.films

data class Pagination(
    val currentPage: Int,
    val totalItems: Int,
    val totalItemsPerPage: Int,
    val totalPages: Int
)