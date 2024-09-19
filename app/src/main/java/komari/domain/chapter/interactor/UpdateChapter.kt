package komari.domain.chapter.interactor

import komari.domain.chapter.ChapterRepository
import komari.domain.chapter.models.ChapterUpdate

class UpdateChapter(
    private val chapterRepository: ChapterRepository,
) {
    suspend fun await(chapter: ChapterUpdate) = chapterRepository.update(chapter)
    suspend fun awaitAll(chapters: List<ChapterUpdate>) = chapterRepository.updateAll(chapters)
}
