package com.drs.auralife.core.network.model.films

import com.drs.auralife.core.network.model.film.Movie

data class Films(
    val items: List<Movie>,
    val pagination: Pagination,
    val status: Boolean,
)
