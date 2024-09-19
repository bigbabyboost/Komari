package Komari.domain.chapter.interactor

import eu.kanade.tachiyomi.domain.manga.models.Manga
import Komari.domain.chapter.ChapterRepository

class GetChapter(
    private val chapterRepository: ChapterRepository,
) {
    suspend fun awaitAll(mangaId: Long, filterScanlators: Boolean) =
        chapterRepository.getChapters(mangaId, filterScanlators)
    suspend fun awaitAll(manga: Manga, filterScanlators: Boolean? = null) =
        awaitAll(manga.id!!, filterScanlators ?: (manga.filtered_scanlators?.isNotEmpty() == true))

    suspend fun awaitById(id: Long) = chapterRepository.getChapterById(id)

    suspend fun awaitAllByUrl(chapterUrl: String, filterScanlators: Boolean) =
        chapterRepository.getChaptersByUrl(chapterUrl, filterScanlators)
    suspend fun awaitByUrl(chapterUrl: String, filterScanlators: Boolean) =
        chapterRepository.getChapterByUrl(chapterUrl, filterScanlators)

    suspend fun awaitAllByUrlAndMangaId(chapterUrl: String, mangaId: Long, filterScanlators: Boolean) =
        chapterRepository.getChaptersByUrlAndMangaId(chapterUrl, mangaId, filterScanlators)
    suspend fun awaitByUrlAndMangaId(chapterUrl: String, mangaId: Long, filterScanlators: Boolean) =
        chapterRepository.getChapterByUrlAndMangaId(chapterUrl, mangaId, filterScanlators)

    fun subscribeAll(mangaId: Long, filterScanlators: Boolean) = chapterRepository.getChaptersAsFlow(mangaId, filterScanlators)
}
