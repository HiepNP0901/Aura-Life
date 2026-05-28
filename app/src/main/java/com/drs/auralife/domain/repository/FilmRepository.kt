package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails

interface FilmRepository {
    suspend fun getLatestFilms(page: Int): List<Film>
    suspend fun getFilmsByCategory(slug: String, page: Int): List<Film>
    suspend fun searchFilms(keyword: String, limit: Int): List<Film>
    suspend fun getFilmDetails(slug: String): FilmDetails
}
