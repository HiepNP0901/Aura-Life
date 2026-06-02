package com.drs.auralife.data.repository

import com.drs.auralife.data.datasource.local.FilmLocalDataSource
import com.drs.auralife.data.datasource.remote.api.FilmApiDataSource
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.PagedResult
import com.drs.auralife.domain.repository.FilmRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FilmRepositoryImpl @javax.inject.Inject constructor(
    private val apiDataSource: FilmApiDataSource,
    private val localDataSource: FilmLocalDataSource,
) : FilmRepository {
    override suspend fun getLatestFilms(page: Int): PagedResult<Film> {
        return try {
            val result = apiDataSource.getLatestFilms(page)
            localDataSource.clearAndInsertFilms(result.data)
            result
        } catch (e: Exception) {
            val cachedFilms = localDataSource.getAllFilms()
            PagedResult(
                data = cachedFilms,
                currentPage = page,
                totalPages = page,
            )
        }
    }

    override suspend fun getFilmsByCategory(slug: String, page: Int): PagedResult<Film> {
        return try {
            val result = apiDataSource.getFilmsByCategory(slug, page)
            localDataSource.clearAndInsertFilms(result.data)
            result
        } catch (e: Exception) {
            val cachedFilms = localDataSource.getAllFilms()
            PagedResult(
                data = cachedFilms,
                currentPage = page,
                totalPages = page,
            )
        }
    }

    override suspend fun searchFilms(keyword: String, limit: Int): List<Film> {
        return try {
            val films = apiDataSource.searchFilms(keyword, limit)
            localDataSource.insertFilms(films)
            films
        } catch (e: Exception) {
            localDataSource.getAllFilms()
        }
    }

    override suspend fun getFilmDetails(slug: String): FilmDetails {
        return try {
            val details = apiDataSource.getFilmDetails(slug)
            localDataSource.insertFilmDetails(details)
            details
        } catch (e: Exception) {
            localDataSource.getFilmDetails(slug) ?: throw e
        }
    }

    override suspend fun getFilmDetailsBatch(slugs: List<String>): Map<String, FilmDetails> {
        val cached = localDataSource.getFilmDetailsBatch(slugs)
        val uncachedSlugs = slugs.filter { it !in cached }

        val uncachedMap = if (uncachedSlugs.isNotEmpty()) {
            coroutineScope {
                uncachedSlugs.map { slug ->
                    async {
                        try {
                            val details = apiDataSource.getFilmDetails(slug)
                            localDataSource.insertFilmDetails(details)
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

        val all = cached + uncachedMap
        return slugs.mapNotNull { slug -> all[slug]?.let { slug to it } }.toMap()
    }
}