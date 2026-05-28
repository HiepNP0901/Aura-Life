package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository

class GetLibraryUseCase(
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(): List<Library> {
        return libraryRepository.getLibraries()
    }
}
