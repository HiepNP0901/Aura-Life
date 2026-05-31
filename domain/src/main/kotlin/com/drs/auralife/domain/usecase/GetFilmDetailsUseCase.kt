package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.repository.FilmRepository

class GetFilmDetailsUseCase @javax.inject.Inject constructor(
    private val filmRepository: FilmRepository,
) {
    suspend operator fun invoke(slug: String): FilmDetails {
        return filmRepository.getFilmDetails(slug)
    }

    suspend fun batch(slugs: List<String>): Map<String, FilmDetails> {
        return filmRepository.getFilmDetailsBatch(slugs)
    }
}
