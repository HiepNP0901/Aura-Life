package com.drs.auralife.data.repository

import com.drs.auralife.data.FilmAPI
import com.drs.auralife.data.local.dao.FilmDao
import com.drs.auralife.data.local.dao.FilmDetailsDao
import com.drs.auralife.data.local.mapper.LocalMapper.toDomainFilm
import com.drs.auralife.data.local.mapper.LocalMapper.toDomainFilmDetails
import com.drs.auralife.data.local.mapper.LocalMapper.toFilmDetailsEntity
import com.drs.auralife.data.local.mapper.LocalMapper.toFilmEntity
import com.drs.auralife.data.mapper.FilmMapper.toDomainFilm as apiToDomainFilm
import com.drs.auralife.data.mapper.FilmMapper.toDomainFilmDetails as apiToDomainFilmDetails
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.PagedResult
import com.drs.auralife.domain.repository.FilmRepository

class FilmRepositoryImpl @javax.inject.Inject constructor(
    private val api: FilmAPI,
    private val filmDao: FilmDao,
    private val filmDetailsDao: FilmDetailsDao,
) : FilmRepository {
    override suspend fun getLatestFilms(page: Int): PagedResult<Film> {
        return try {
            val response = api.getLatestFilms(page)
            val films = response.items.map { it.apiToDomainFilm() }
            filmDao.clearFilms()
            filmDao.insertFilms(films.map { it.toFilmEntity() })
            PagedResult(
                data = films,
                currentPage = response.pagination.currentPage,
                totalPages = response.pagination.totalPages,
            )
        } catch (e: Exception) {
            val cachedFilms = filmDao.getAllFilms().map { it.toDomainFilm() }
            PagedResult(
                data = cachedFilms,
                currentPage = page,
                totalPages = page,
            )
        }
    }

    override suspend fun getFilmsByCategory(slug: String, page: Int): PagedResult<Film> {
        return try {
            val response = api.getFilmsByCategory(slug, page)
            val cdn = response.data.appDomainCdnImage
            val films = response.data.items.map { movie ->
                movie.posterUrl = cdn + "/" + movie.posterUrl
                movie.thumbUrl = cdn + "/" + movie.thumbUrl
                movie.apiToDomainFilm()
            }
            filmDao.clearFilms()
            filmDao.insertFilms(films.map { it.toFilmEntity() })
            PagedResult(
                data = films,
                currentPage = response.data.params.pagination.currentPage,
                totalPages = response.data.params.pagination.totalPages,
            )
        } catch (e: Exception) {
            val cachedFilms = filmDao.getAllFilms().map { it.toDomainFilm() }
            PagedResult(
                data = cachedFilms,
                currentPage = page,
                totalPages = page,
            )
        }
    }

    override suspend fun searchFilms(keyword: String, limit: Int): List<Film> {
        val response = api.searchFilms(keyword, limit)
        val cdn = response.data.appDomainCdnImage
        return response.data.items.map { movie ->
            movie.posterUrl = cdn + "/" + movie.posterUrl
            movie.thumbUrl = cdn + "/" + movie.thumbUrl
            movie.apiToDomainFilm()
        }
    }

    override suspend fun getFilmDetails(slug: String): FilmDetails {
        return try {
            val details = api.getFilmDetails(slug).apiToDomainFilmDetails()
            filmDetailsDao.insertFilmDetails(details.toFilmDetailsEntity())
            details
        } catch (e: Exception) {
            val cached = filmDetailsDao.getFilmDetails(slug)
            cached?.toDomainFilmDetails() ?: throw e
        }
    }
}
