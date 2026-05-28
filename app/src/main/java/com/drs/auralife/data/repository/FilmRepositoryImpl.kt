package com.drs.auralife.data.repository

import com.drs.auralife.data.FilmAPI
import com.drs.auralife.data.mapper.FilmMapper.toDomainFilm
import com.drs.auralife.data.mapper.FilmMapper.toDomainFilmDetails
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.PagedResult
import com.drs.auralife.domain.repository.FilmRepository

class FilmRepositoryImpl @javax.inject.Inject constructor(
    private val api: FilmAPI,
) : FilmRepository {
    override suspend fun getLatestFilms(page: Int): PagedResult<Film> {
        val response = api.getLatestFilms(page)
        return PagedResult(
            data = response.items.map { it.toDomainFilm() },
            currentPage = response.pagination.currentPage,
            totalPages = response.pagination.totalPages,
        )
    }

    override suspend fun getFilmsByCategory(slug: String, page: Int): PagedResult<Film> {
        val response = api.getFilmsByCategory(slug, page)
        val cdn = response.data.appDomainCdnImage
        val films = response.data.items.map { movie ->
            movie.posterUrl = cdn + "/" + movie.posterUrl
            movie.thumbUrl = cdn + "/" + movie.thumbUrl
            movie.toDomainFilm()
        }
        return PagedResult(
            data = films,
            currentPage = response.data.params.pagination.currentPage,
            totalPages = response.data.params.pagination.totalPages,
        )
    }

    override suspend fun searchFilms(keyword: String, limit: Int): List<Film> {
        val response = api.searchFilms(keyword, limit)
        val cdn = response.data.appDomainCdnImage
        return response.data.items.map { movie ->
            movie.posterUrl = cdn + "/" + movie.posterUrl
            movie.thumbUrl = cdn + "/" + movie.thumbUrl
            movie.toDomainFilm()
        }
    }

    override suspend fun getFilmDetails(slug: String): FilmDetails {
        return api.getFilmDetails(slug).toDomainFilmDetails()
    }
}
