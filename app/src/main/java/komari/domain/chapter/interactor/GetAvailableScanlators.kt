package komari.domain.chapter.interactor

import komari.domain.chapter.ChapterRepository

class GetAvailableScanlators(
    private val chapterRepository: ChapterRepository,
) {
    suspend fun await(mangaId: Long) = chapterRepository.getScanlatorsByChapter(mangaId)
    fun subscribe(mangaId: Long) = chapterRepository.getScanlatorsByChapterAsFlow(mangaId)
}
