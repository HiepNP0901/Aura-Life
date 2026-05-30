package com.drs.auralife.data.repository

import com.drs.auralife.data.local.dao.FilmDao
import com.drs.auralife.data.local.dao.FilmDetailsDao
import com.drs.auralife.data.local.mapper.LocalMapper.toDomainFilm
import com.drs.auralife.data.local.mapper.LocalMapper.toDomainFilmDetails
import com.drs.auralife.data.local.mapper.LocalMapper.toFilmDetailsEntity
import com.drs.auralife.data.local.mapper.LocalMapper.toFilmEntity
import com.drs.auralife.data.remote.api.FilmAPI
import com.drs.auralife.data.remote.api.FilmMapper.toDomainFilm as apiToDomainFilm
import com.drs.auralife.data.remote.api.FilmMapper.toDomainFilmDetails as apiToDomainFilmDetails
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.PagedResult
import com.drs.auralife.domain.repository.FilmRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
                movie.apiToDomainFilm().copy(
                    posterUrl = "$cdn/${movie.posterUrl}",
                    thumbUrl = "$cdn/${movie.thumbUrl}",
                )
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
        return try {
            val response = api.searchFilms(keyword, limit)
            val cdn = response.data.appDomainCdnImage
            val films = response.data.items.map { movie ->
                movie.apiToDomainFilm().copy(
                    posterUrl = "$cdn/${movie.posterUrl}",
                    thumbUrl = "$cdn/${movie.thumbUrl}",
                )
            }
            filmDao.insertFilms(films.map { it.toFilmEntity() })
            films
        } catch (e: Exception) {
            filmDao.getAllFilms().map { it.toDomainFilm() }
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

    override suspend fun getFilmDetailsBatch(slugs: List<String>): Map<String, FilmDetails> {
        val cached = filmDetailsDao.getFilmDetailsBatch(slugs)
        val cachedMap = cached.associateBy { it.slug }
        val uncachedSlugs = slugs.filter { it !in cachedMap }

        val uncachedMap = if (uncachedSlugs.isNotEmpty()) {
            coroutineScope {
                uncachedSlugs.map { slug ->
                    async {
                        try {
                            val details = api.getFilmDetails(slug).apiToDomainFilmDetails()
                            filmDetailsDao.insertFilmDetails(details.toFilmDetailsEntity())
                            slug to details
                        } catch (_: Exception) {
                            null
                        }
                    }
                }.mapNotNull { it.await() }.toMap()
            }
        } else {
            emptyMap()
        }

        val all = cachedMap.mapValues { it.value.toDomainFilmDetails() } + uncachedMap
        return slugs.mapNotNull { slug -> all[slug]?.let { slug to it } }.toMap()
    }
}
