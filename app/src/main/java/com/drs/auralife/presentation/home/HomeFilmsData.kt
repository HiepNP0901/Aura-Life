package com.drs.auralife.presentation.home

import com.drs.auralife.domain.model.Film

data class HomeFilmsData(
    val films: List<Film>,
    val totalPages: Int,
)
