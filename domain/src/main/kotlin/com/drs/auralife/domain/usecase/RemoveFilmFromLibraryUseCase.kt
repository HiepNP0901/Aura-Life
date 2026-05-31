package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.LibraryRepository

class RemoveFilmFromLibraryUseCase @javax.inject.Inject constructor(
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(libraryName: String, slug: String): Boolean {
        return libraryRepository.removeFilmFromLibrary(libraryName, slug)
    }
}
