package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository

class GetLibraryUseCase @javax.inject.Inject constructor(
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(): Result<List<Library>> {
        return libraryRepository.getLibraries()
    }
}
