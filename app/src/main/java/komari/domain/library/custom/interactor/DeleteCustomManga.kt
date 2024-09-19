package Komari.domain.library.custom.interactor

import Komari.domain.library.custom.CustomMangaRepository

class DeleteCustomManga(
    private val customMangaRepository: CustomMangaRepository,
) {
    suspend fun await(mangaId: Long) = customMangaRepository.deleteCustomManga(mangaId)
    suspend fun bulk(mangaIds: List<Long>) = customMangaRepository.deleteBulkCustomManga(mangaIds)
}
