package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.repository.FilmRepository
import com.drs.auralife.domain.result.Result

class GetFilmDetailsUseCase @javax.inject.Inject constructor(
    private val filmRepository: FilmRepository,
) {
    suspend operator fun invoke(slug: String): Result<FilmDetails> {
        return filmRepository.getFilmDetails(slug)
    }

    suspend fun batch(slugs: List<String>): Result<Map<String, FilmDetails>> {
        return filmRepository.getFilmDetailsBatch(slugs)
    }
}
