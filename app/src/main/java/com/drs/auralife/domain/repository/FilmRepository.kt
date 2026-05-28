package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.PagedResult

interface FilmRepository {
    suspend fun getLatestFilms(page: Int): PagedResult<Film>
    suspend fun getFilmsByCategory(slug: String, page: Int): PagedResult<Film>
    suspend fun searchFilms(keyword: String, limit: Int): List<Film>
    suspend fun getFilmDetails(slug: String): FilmDetails
}
