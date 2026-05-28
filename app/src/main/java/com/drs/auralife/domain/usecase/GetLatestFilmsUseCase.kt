package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.repository.FilmRepository

class GetLatestFilmsUseCase(
    private val filmRepository: FilmRepository,
) {
    suspend operator fun invoke(page: Int): List<Film> {
        return filmRepository.getLatestFilms(page)
    }
}
