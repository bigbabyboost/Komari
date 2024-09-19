package Komari.domain.extension.repo.interactor

import Komari.domain.extension.repo.ExtensionRepoRepository

class DeleteExtensionRepo(
    private val extensionRepoRepository: ExtensionRepoRepository
) {
    suspend fun await(baseUrl: String) {
        extensionRepoRepository.deleteRepository(baseUrl)
    }
}
