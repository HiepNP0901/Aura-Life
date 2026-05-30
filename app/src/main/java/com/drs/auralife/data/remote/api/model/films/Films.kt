package com.drs.auralife.data.remote.api.model.films

import com.drs.auralife.data.remote.api.model.film.Movie

data class Films(
    val items: List<Movie>,
    val pagination: Pagination,
    val status: Boolean,
)
