package komari.domain.extension.repo.interactor

import komari.domain.extension.repo.ExtensionRepoRepository
import komari.domain.extension.repo.model.ExtensionRepo

class ReplaceExtensionRepo(
    private val extensionRepoRepository: ExtensionRepoRepository
) {
    suspend fun await(repo: ExtensionRepo) {
        extensionRepoRepository.replaceRepository(repo)
    }
}
