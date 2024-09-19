package Komari.domain.extension.repo.interactor

import kotlinx.coroutines.flow.Flow
import Komari.domain.extension.repo.ExtensionRepoRepository
import Komari.domain.extension.repo.model.ExtensionRepo

class GetExtensionRepo(
    private val extensionRepoRepository: ExtensionRepoRepository
) {
    fun subscribeAll(): Flow<List<ExtensionRepo>> = extensionRepoRepository.subscribeAll()

    suspend fun getAll(): List<ExtensionRepo> = extensionRepoRepository.getAll()
}
