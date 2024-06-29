package eu.kanade.tachiyomi.data.backup.models

import eu.kanade.tachiyomi.data.database.models.ChapterImpl
import eu.kanade.tachiyomi.data.library.CustomMangaManager
import eu.kanade.tachiyomi.source.model.UpdateStrategy
import eu.kanade.tachiyomi.util.chapter.ChapterUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import yokai.data.manga.models.readingModeType
import yokai.domain.library.custom.model.CustomMangaInfo
import yokai.domain.manga.models.Manga
import yokai.domain.track.models.Track

@Suppress("DEPRECATION")
@Serializable
data class BackupManga(
    // in 1.x some of these values have different names
    @ProtoNumber(1) var source: Long,
    // url is called key in 1.x
    @ProtoNumber(2) var url: String,
    @ProtoNumber(3) var title: String = "",
    @ProtoNumber(4) var artist: String? = null,
    @ProtoNumber(5) var author: String? = null,
    @ProtoNumber(6) var description: String? = null,
    @ProtoNumber(7) var genre: List<String> = emptyList(),
    @ProtoNumber(8) var status: Int = 0,
    // thumbnailUrl is called cover in 1.x
    @ProtoNumber(9) var thumbnailUrl: String? = null,
    // @ProtoNumber(10) val customCover: String = "", 1.x value, not used in 0.x
    // @ProtoNumber(11) val lastUpdate: Long = 0, 1.x value, not used in 0.x
    // @ProtoNumber(12) val lastInit: Long = 0, 1.x value, not used in 0.x
    @ProtoNumber(13) var dateAdded: Long = 0,
    @ProtoNumber(14) var viewer: Int = 0, // Replaced by viewer_flags
    // @ProtoNumber(15) val flags: Int = 0, 1.x value, not used in 0.x
    @ProtoNumber(16) var chapters: List<BackupChapter> = emptyList(),
    @ProtoNumber(17) var categories: List<Int> = emptyList(),
    @ProtoNumber(18) var tracking: List<BackupTracking> = emptyList(),
    // Bump by 100 for values that are not saved/implemented in 1.x but are used in 0.x
    @ProtoNumber(100) var favorite: Boolean = true,
    @ProtoNumber(101) var chapterFlags: Int = 0,
    @ProtoNumber(102) var brokenHistory: List<BrokenBackupHistory> = emptyList(),
    @ProtoNumber(103) var viewer_flags: Int? = null,
    @ProtoNumber(104) var history: List<BackupHistory> = emptyList(),
    @ProtoNumber(105) var updateStrategy: UpdateStrategy = UpdateStrategy.ALWAYS_UPDATE,
    @ProtoNumber(108) var excludedScanlators: List<String> = emptyList(),

    // SY specific values
    @ProtoNumber(602) var customStatus: Int = 0,

    // J2K specific values
    @ProtoNumber(800) var customTitle: String? = null,
    @ProtoNumber(801) var customArtist: String? = null,
    @ProtoNumber(802) var customAuthor: String? = null,
    // skipping 803 due to using duplicate value in previous builds
    @ProtoNumber(804) var customDescription: String? = null,
    @ProtoNumber(805) var customGenre: List<String>? = null,
) {
    fun getMangaImpl(): Manga {
        return Manga(
            id = null,
            url = this@BackupManga.url,
            ogTitle = this@BackupManga.title,
            ogArtist = this@BackupManga.artist,
            ogAuthor = this@BackupManga.author,
            ogDescription = this@BackupManga.description,
            ogGenres = this@BackupManga.genre,
            ogStatus = this@BackupManga.status,
            thumbnailUrl = this@BackupManga.thumbnailUrl,
            favorite = this@BackupManga.favorite,
            source = this@BackupManga.source,
            dateAdded = this@BackupManga.dateAdded,
            viewerFlags = (
                this@BackupManga.viewer_flags
                    ?: this@BackupManga.viewer
                ).takeIf { it != 0 }
                ?: -1,
            chapterFlags = this@BackupManga.chapterFlags,
            updateStrategy = this@BackupManga.updateStrategy,
        )
    }

    fun getChaptersImpl(): List<ChapterImpl> {
        return chapters.map {
            it.toChapterImpl()
        }
    }

    fun getCustomMangaInfo(): CustomMangaInfo? {
        if (customTitle != null ||
            customArtist != null ||
            customAuthor != null ||
            customDescription != null ||
            customGenre != null ||
            customStatus != 0
        ) {
            return CustomMangaInfo(
                mangaId= 0L,
                title = customTitle,
                author = customAuthor,
                artist = customArtist,
                description = customDescription,
                genre = customGenre?.joinToString(),
                status = customStatus,
            )
        }
        return null
    }

    fun getTrackingImpl(): List<Track> {
        return tracking.map {
            it.getTrackingImpl()
        }
    }

    companion object {
        fun copyFrom(manga: Manga, customMangaManager: CustomMangaManager?): BackupManga {
            return BackupManga(
                url = manga.url,
                title = manga.ogTitle,
                artist = manga.ogArtist,
                author = manga.ogAuthor,
                description = manga.ogDescription,
                genre = manga.ogGenres,
                status = manga.ogStatus,
                thumbnailUrl = manga.thumbnailUrl,
                favorite = manga.favorite,
                source = manga.source,
                dateAdded = manga.dateAdded,
                viewer = manga.readingModeType,
                viewer_flags = manga.viewerFlags.takeIf { it != -1 } ?: 0,
                chapterFlags = manga.chapterFlags,
                updateStrategy = manga.updateStrategy,
                excludedScanlators = ChapterUtil.getScanlators(manga.filteredScanlators),
            ).also { backupManga ->
                customMangaManager?.getManga(manga.id)?.let {
                    backupManga.customTitle = it.title
                    backupManga.customArtist = it.artist
                    backupManga.customAuthor = it.author
                    backupManga.customDescription = it.description
                    backupManga.customGenre = it.genres
                    backupManga.customStatus = it.status ?: -1
                }
            }
        }
    }
}
