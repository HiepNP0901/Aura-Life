package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.repository.FilmRepository

class SearchFilmsUseCase(
    private val filmRepository: FilmRepository,
) {
    suspend operator fun invoke(keyword: String, limit: Int): List<Film> {
        return filmRepository.searchFilms(keyword, limit)
    }
}
