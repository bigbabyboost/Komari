package Komari.core.di

import org.koin.dsl.module
import Komari.data.category.CategoryRepositoryImpl
import Komari.data.chapter.ChapterRepositoryImpl
import Komari.data.extension.repo.ExtensionRepoRepositoryImpl
import Komari.data.history.HistoryRepositoryImpl
import Komari.data.library.custom.CustomMangaRepositoryImpl
import Komari.data.manga.MangaRepositoryImpl
import Komari.domain.category.CategoryRepository
import Komari.domain.category.interactor.GetCategories
import Komari.domain.chapter.ChapterRepository
import Komari.domain.chapter.interactor.DeleteChapter
import Komari.domain.chapter.interactor.GetAvailableScanlators
import Komari.domain.chapter.interactor.GetChapter
import Komari.domain.chapter.interactor.InsertChapter
import Komari.domain.chapter.interactor.UpdateChapter
import Komari.domain.extension.interactor.TrustExtension
import Komari.domain.extension.repo.ExtensionRepoRepository
import Komari.domain.extension.repo.interactor.CreateExtensionRepo
import Komari.domain.extension.repo.interactor.DeleteExtensionRepo
import Komari.domain.extension.repo.interactor.GetExtensionRepo
import Komari.domain.extension.repo.interactor.GetExtensionRepoCount
import Komari.domain.extension.repo.interactor.ReplaceExtensionRepo
import Komari.domain.extension.repo.interactor.UpdateExtensionRepo
import Komari.domain.history.HistoryRepository
import Komari.domain.library.custom.CustomMangaRepository
import Komari.domain.library.custom.interactor.CreateCustomManga
import Komari.domain.library.custom.interactor.DeleteCustomManga
import Komari.domain.library.custom.interactor.GetCustomManga
import Komari.domain.library.custom.interactor.RelinkCustomManga
import Komari.domain.manga.MangaRepository
import Komari.domain.manga.interactor.GetLibraryManga
import Komari.domain.manga.interactor.GetManga
import Komari.domain.manga.interactor.InsertManga
import Komari.domain.manga.interactor.UpdateManga
import Komari.domain.recents.interactor.GetRecents

fun domainModule() = module {
    factory { TrustExtension(get(), get()) }

    single<ExtensionRepoRepository> { ExtensionRepoRepositoryImpl(get()) }
    factory { CreateExtensionRepo(get()) }
    factory { DeleteExtensionRepo(get()) }
    factory { GetExtensionRepo(get()) }
    factory { GetExtensionRepoCount(get()) }
    factory { ReplaceExtensionRepo(get()) }
    factory { UpdateExtensionRepo(get(), get()) }

    single<CustomMangaRepository> { CustomMangaRepositoryImpl(get()) }
    factory { CreateCustomManga(get()) }
    factory { DeleteCustomManga(get()) }
    factory { GetCustomManga(get()) }
    factory { RelinkCustomManga(get()) }

    single<MangaRepository> { MangaRepositoryImpl(get()) }
    factory { GetManga(get()) }
    factory { GetLibraryManga(get()) }
    factory { InsertManga(get()) }
    factory { UpdateManga(get()) }

    single<ChapterRepository> { ChapterRepositoryImpl(get()) }
    factory { DeleteChapter(get()) }
    factory { GetAvailableScanlators(get()) }
    factory { GetChapter(get()) }
    factory { InsertChapter(get()) }
    factory { UpdateChapter(get()) }

    single<HistoryRepository> { HistoryRepositoryImpl(get()) }

    factory { GetRecents(get(), get()) }

    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    factory { GetCategories(get()) }
}
