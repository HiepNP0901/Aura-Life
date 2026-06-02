package com.drs.auralife.data.datasource.local

import com.drs.auralife.core.database.dao.FilmDao
import com.drs.auralife.core.database.dao.FilmDetailsDao
import com.drs.auralife.core.database.mapper.LocalMapper.toDomainFilm
import com.drs.auralife.core.database.mapper.LocalMapper.toDomainFilmDetails
import com.drs.auralife.core.database.mapper.LocalMapper.toFilmDetailsEntity
import com.drs.auralife.core.database.mapper.LocalMapper.toFilmEntity
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import javax.inject.Inject

class FilmLocalDataSource @Inject constructor(
    private val filmDao: FilmDao,
    private val filmDetailsDao: FilmDetailsDao,
) {
    suspend fun getAllFilms(): List<Film> {
        return filmDao.getAllFilms().map { it.toDomainFilm() }
    }

    suspend fun clearAndInsertFilms(films: List<Film>) {
        filmDao.clearFilms()
        filmDao.insertFilms(films.map { it.toFilmEntity() })
    }

    suspend fun insertFilms(films: List<Film>) {
        filmDao.insertFilms(films.map { it.toFilmEntity() })
    }

    suspend fun getFilmDetails(slug: String): FilmDetails? {
        return filmDetailsDao.getFilmDetails(slug)?.toDomainFilmDetails()
    }

    suspend fun insertFilmDetails(details: FilmDetails) {
        filmDetailsDao.insertFilmDetails(details.toFilmDetailsEntity())
    }

    suspend fun getFilmDetailsBatch(slugs: List<String>): Map<String, FilmDetails> {
        return filmDetailsDao.getFilmDetailsBatch(slugs)
            .associateBy { it.slug }
            .mapValues { it.value.toDomainFilmDetails() }
    }
}
