package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.repository.FilmRepository
import com.drs.auralife.domain.result.Result

class SearchFilmsUseCase @javax.inject.Inject constructor(
    private val filmRepository: FilmRepository,
) {
    suspend operator fun invoke(keyword: String, limit: Int): Result<List<Film>> {
        return filmRepository.searchFilms(keyword, limit)
    }
}
