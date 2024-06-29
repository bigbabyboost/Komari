package eu.kanade.tachiyomi.util.manga

import eu.kanade.tachiyomi.util.chapter.ChapterUtil
import yokai.domain.manga.interactor.UpdateManga
import yokai.domain.manga.models.Manga
import yokai.domain.manga.models.MangaUpdate

object MangaUtil {
    suspend fun setScanlatorFilter(updateManga: UpdateManga, manga: Manga, filteredScanlators: Set<String>) {
        if (manga.id == null) return

        updateManga.await(MangaUpdate(
            manga.id!!,
            filteredScanlators = if (filteredScanlators.isEmpty()) null else ChapterUtil.getScanlatorString(
                filteredScanlators,
            ),
        ))
    }
}
