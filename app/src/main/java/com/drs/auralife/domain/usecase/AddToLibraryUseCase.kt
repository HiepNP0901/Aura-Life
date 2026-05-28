package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository

class AddToLibraryUseCase @javax.inject.Inject constructor(
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(library: Library): Boolean {
        return libraryRepository.addToLibrary(library)
    }
}
