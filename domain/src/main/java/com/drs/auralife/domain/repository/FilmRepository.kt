package com.drs.auralife.domain.repository

import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.PagedResult

interface FilmRepository {
    suspend fun getLatestFilms(page: Int): Result<PagedResult<Film>>
    suspend fun getFilmsByCategory(slug: String, page: Int): Result<PagedResult<Film>>
    suspend fun searchFilms(keyword: String, limit: Int): Result<List<Film>>
    suspend fun getFilmDetails(slug: String): Result<FilmDetails>
    suspend fun getFilmDetailsBatch(slugs: List<String>): Result<Map<String, FilmDetails>>
}
