package Komari.domain.chapter.interactor

import Komari.domain.chapter.ChapterRepository
import Komari.domain.chapter.models.ChapterUpdate

class UpdateChapter(
    private val chapterRepository: ChapterRepository,
) {
    suspend fun await(chapter: ChapterUpdate) = chapterRepository.update(chapter)
    suspend fun awaitAll(chapters: List<ChapterUpdate>) = chapterRepository.updateAll(chapters)
}
