package com.drs.auralife.data.datasource.remote.api

import com.drs.auralife.data.remote.api.FilmAPI
import com.drs.auralife.data.remote.api.FilmMapper.toDomainFilm
import com.drs.auralife.data.remote.api.FilmMapper.toDomainFilmDetails
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.PagedResult
import javax.inject.Inject

class FilmApiDataSource @Inject constructor(
    private val api: FilmAPI,
) {
    suspend fun getLatestFilms(page: Int): PagedResult<Film> {
        val response = api.getLatestFilms(page)
        val films = response.items.map { it.toDomainFilm() }
        return PagedResult(
            data = films,
            currentPage = response.pagination.currentPage,
            totalPages = response.pagination.totalPages,
        )
    }

    suspend fun getFilmsByCategory(slug: String, page: Int): PagedResult<Film> {
        val response = api.getFilmsByCategory(slug, page)
        val cdn = response.data.appDomainCdnImage
        val films = response.data.items.map { movie ->
            movie.toDomainFilm().copy(
                posterUrl = "$cdn/${movie.posterUrl}",
                thumbUrl = "$cdn/${movie.thumbUrl}",
            )
        }
        return PagedResult(
            data = films,
            currentPage = response.data.params.pagination.currentPage,
            totalPages = response.data.params.pagination.totalPages,
        )
    }

    suspend fun searchFilms(keyword: String, limit: Int): List<Film> {
        val response = api.searchFilms(keyword, limit)
        val cdn = response.data.appDomainCdnImage
        return response.data.items.map { movie ->
            movie.toDomainFilm().copy(
                posterUrl = "$cdn/${movie.posterUrl}",
                thumbUrl = "$cdn/${movie.thumbUrl}",
            )
        }
    }

    suspend fun getFilmDetails(slug: String): FilmDetails {
        return api.getFilmDetails(slug).toDomainFilmDetails()
    }
}