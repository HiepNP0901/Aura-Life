package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.repository.FilmRepository

class GetFilmsByCategoryUseCase @javax.inject.Inject constructor(
    private val filmRepository: FilmRepository,
) {
    suspend operator fun invoke(slug: String, page: Int): List<Film> {
        return filmRepository.getFilmsByCategory(slug, page)
    }
}
