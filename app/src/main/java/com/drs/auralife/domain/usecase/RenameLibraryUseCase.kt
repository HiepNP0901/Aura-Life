package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.LibraryRepository

class RenameLibraryUseCase(
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(oldName: String, newName: String): Boolean {
        return libraryRepository.renameLibrary(oldName, newName)
    }
}
