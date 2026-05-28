package com.drs.auralife.domain.model

data class PagedResult<T>(
    val data: List<T>,
    val currentPage: Int,
    val totalPages: Int,
)
