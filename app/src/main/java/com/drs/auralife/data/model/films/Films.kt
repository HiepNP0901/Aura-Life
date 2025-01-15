package com.drs.auralife.data.model.films

import com.drs.auralife.data.model.film.Movie

data class Films(
    val items: List<Movie>,
    val pagination: Pagination,
    val status: Boolean
)