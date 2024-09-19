package Komari.domain.extension.repo.interactor

import Komari.domain.extension.repo.ExtensionRepoRepository
import Komari.domain.extension.repo.model.ExtensionRepo

class ReplaceExtensionRepo(
    private val extensionRepoRepository: ExtensionRepoRepository
) {
    suspend fun await(repo: ExtensionRepo) {
        extensionRepoRepository.replaceRepository(repo)
    }
}
