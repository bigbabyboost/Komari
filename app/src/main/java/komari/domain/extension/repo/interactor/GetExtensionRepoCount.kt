package komari.domain.extension.repo.interactor

import komari.domain.extension.repo.ExtensionRepoRepository

class GetExtensionRepoCount(
    private val extensionRepoRepository: ExtensionRepoRepository
) {
    fun subscribe() = extensionRepoRepository.getCount()
}
