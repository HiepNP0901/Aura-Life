package com.drs.auralife.data.repository

import com.drs.auralife.data.FilmAPI
import com.drs.auralife.data.mapper.FilmMapper.toDomainFilm
import com.drs.auralife.data.mapper.FilmMapper.toDomainFilmDetails
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.repository.FilmRepository

class FilmRepositoryImpl(
    private val api: FilmAPI,
) : FilmRepository {
    override suspend fun getLatestFilms(page: Int): List<Film> {
        return api.getLatestFilms(page).items.map { it.toDomainFilm() }
    }

    override suspend fun getFilmsByCategory(slug: String, page: Int): List<Film> {
        return api.getFilmsByCategory(slug, page).data.items.map { it.toDomainFilm() }
    }

    override suspend fun searchFilms(keyword: String, limit: Int): List<Film> {
        return api.searchFilms(keyword, limit).data.items.map { it.toDomainFilm() }
    }

    override suspend fun getFilmDetails(slug: String): FilmDetails {
        return api.getFilmDetails(slug).toDomainFilmDetails()
    }
}
