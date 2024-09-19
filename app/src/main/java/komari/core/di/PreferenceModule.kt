package Komari.core.di

import android.app.Application
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.core.preference.AndroidPreferenceStore
import eu.kanade.tachiyomi.core.preference.PreferenceStore
import eu.kanade.tachiyomi.core.security.SecurityPreferences
import eu.kanade.tachiyomi.core.storage.AndroidStorageFolderProvider
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.track.TrackPreferences
import eu.kanade.tachiyomi.network.NetworkPreferences
import org.koin.dsl.module
import Komari.domain.backup.BackupPreferences
import Komari.domain.base.BasePreferences
import Komari.domain.download.DownloadPreferences
import Komari.domain.recents.RecentsPreferences
import Komari.domain.source.SourcePreferences
import Komari.domain.storage.StoragePreferences
import Komari.domain.ui.UiPreferences
import Komari.domain.ui.settings.ReaderPreferences

fun preferenceModule(application: Application) = module {
    single<PreferenceStore> { AndroidPreferenceStore(application) }

    single { BasePreferences(get()) }

    single { SourcePreferences(get()) }

    single { TrackPreferences(get()) }

    single { UiPreferences(get()) }

    single { ReaderPreferences(get()) }

    single { RecentsPreferences(get()) }

    single { DownloadPreferences(get()) }

    single {
        NetworkPreferences(
            get(),
            BuildConfig.FLAVOR == "dev" || BuildConfig.DEBUG || BuildConfig.NIGHTLY,
        )
    }

    single { SecurityPreferences(get()) }

    single { BackupPreferences(get()) }

    single {
        PreferencesHelper(
            context = application,
            preferenceStore = get(),
        )
    }

    single {
        StoragePreferences(
            folderProvider = get<AndroidStorageFolderProvider>(),
            preferenceStore = get(),
        )
    }
}
