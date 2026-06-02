package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository
import com.drs.auralife.domain.result.Result

class GetLibraryUseCase @javax.inject.Inject constructor(
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(): Result<List<Library>> {
        return libraryRepository.getLibraries()
    }
}
