package com.drs.auralife.data.repository

import com.drs.auralife.domain.result.Result
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
    override suspend fun getLatestFilms(page: Int): Result<PagedResult<Film>> {
        return try {
            val result = apiDataSource.getLatestFilms(page)
            localDataSource.clearAndInsertFilms(result.data)
            Result.Success(result)
        } catch (e: Exception) {
            val cachedFilms = localDataSource.getAllFilms()
            if (cachedFilms.isNotEmpty()) {
                Result.Success(PagedResult(data = cachedFilms, currentPage = page, totalPages = page))
            } else {
                Result.Error(e)
            }
        }
    }

    override suspend fun getFilmsByCategory(slug: String, page: Int): Result<PagedResult<Film>> {
        return try {
            val result = apiDataSource.getFilmsByCategory(slug, page)
            localDataSource.clearAndInsertFilms(result.data)
            Result.Success(result)
        } catch (e: Exception) {
            val cachedFilms = localDataSource.getAllFilms()
            if (cachedFilms.isNotEmpty()) {
                Result.Success(PagedResult(data = cachedFilms, currentPage = page, totalPages = page))
            } else {
                Result.Error(e)
            }
        }
    }

    override suspend fun searchFilms(keyword: String, limit: Int): Result<List<Film>> {
        return try {
            val films = apiDataSource.searchFilms(keyword, limit)
            localDataSource.insertFilms(films)
            Result.Success(films)
        } catch (e: Exception) {
            val cached = localDataSource.getAllFilms()
            if (cached.isNotEmpty()) Result.Success(cached) else Result.Error(e)
        }
    }

    override suspend fun getFilmDetails(slug: String): Result<FilmDetails> {
        return try {
            val details = apiDataSource.getFilmDetails(slug)
            localDataSource.insertFilmDetails(details)
            Result.Success(details)
        } catch (e: Exception) {
            val cached = localDataSource.getFilmDetails(slug)
            if (cached != null) Result.Success(cached) else Result.Error(e)
        }
    }

    override suspend fun getFilmDetailsBatch(slugs: List<String>): Result<Map<String, FilmDetails>> {
        return try {
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
            val result = slugs.mapNotNull { slug -> all[slug]?.let { slug to it } }.toMap()
            Result.Success(result)
        } catch (e: Exception) {
            val cached = localDataSource.getFilmDetailsBatch(slugs)
            if (cached.isNotEmpty()) Result.Success(cached) else Result.Error(e)
        }
    }
}
