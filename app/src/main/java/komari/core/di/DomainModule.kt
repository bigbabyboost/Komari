package komari.core.di

import org.koin.dsl.module
import komari.data.category.CategoryRepositoryImpl
import komari.data.chapter.ChapterRepositoryImpl
import komari.data.extension.repo.ExtensionRepoRepositoryImpl
import komari.data.history.HistoryRepositoryImpl
import komari.data.library.custom.CustomMangaRepositoryImpl
import komari.data.manga.MangaRepositoryImpl
import komari.domain.category.CategoryRepository
import komari.domain.category.interactor.GetCategories
import komari.domain.chapter.ChapterRepository
import komari.domain.chapter.interactor.DeleteChapter
import komari.domain.chapter.interactor.GetAvailableScanlators
import komari.domain.chapter.interactor.GetChapter
import komari.domain.chapter.interactor.InsertChapter
import komari.domain.chapter.interactor.UpdateChapter
import komari.domain.extension.interactor.TrustExtension
import komari.domain.extension.repo.ExtensionRepoRepository
import komari.domain.extension.repo.interactor.CreateExtensionRepo
import komari.domain.extension.repo.interactor.DeleteExtensionRepo
import komari.domain.extension.repo.interactor.GetExtensionRepo
import komari.domain.extension.repo.interactor.GetExtensionRepoCount
import komari.domain.extension.repo.interactor.ReplaceExtensionRepo
import komari.domain.extension.repo.interactor.UpdateExtensionRepo
import komari.domain.history.HistoryRepository
import komari.domain.library.custom.CustomMangaRepository
import komari.domain.library.custom.interactor.CreateCustomManga
import komari.domain.library.custom.interactor.DeleteCustomManga
import komari.domain.library.custom.interactor.GetCustomManga
import komari.domain.library.custom.interactor.RelinkCustomManga
import komari.domain.manga.MangaRepository
import komari.domain.manga.interactor.GetLibraryManga
import komari.domain.manga.interactor.GetManga
import komari.domain.manga.interactor.InsertManga
import komari.domain.manga.interactor.UpdateManga
import komari.domain.recents.interactor.GetRecents

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
