package komari.domain.library.custom.interactor

import komari.domain.library.custom.CustomMangaRepository

class RelinkCustomManga(
    private val customMangaRepository: CustomMangaRepository,
) {
    suspend fun await(oldId: Long, newId: Long) = customMangaRepository.relinkCustomManga(oldId, newId)
}
