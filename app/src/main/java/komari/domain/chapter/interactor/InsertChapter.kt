package komari.domain.chapter.interactor

import eu.kanade.tachiyomi.data.database.models.Chapter
import komari.domain.chapter.ChapterRepository

class InsertChapter(
    private val chapterRepository: ChapterRepository,
) {
    suspend fun await(chapter: Chapter) = chapterRepository.insert(chapter)

    suspend fun awaitBulk(chapters: List<Chapter>) = chapterRepository.insertBulk(chapters)
}
