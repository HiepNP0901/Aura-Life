package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.PagedResult
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.repository.FilmRepository

class GetLatestFilmsUseCase @javax.inject.Inject constructor(
    private val filmRepository: FilmRepository,
) {
    suspend operator fun invoke(page: Int): PagedResult<Film> {
        return filmRepository.getLatestFilms(page)
    }
}
