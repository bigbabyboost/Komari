package komari.core.di

import android.app.Application
import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.core.storage.AndroidStorageFolderProvider
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.DbOpenCallback
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.library.CustomMangaManager
import eu.kanade.tachiyomi.data.track.TrackManager
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.network.JavaScriptEngine
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.util.chapter.ChapterFilter
import eu.kanade.tachiyomi.util.manga.MangaShortcutManager
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import komari.data.AndroidDatabaseHandler
import komari.data.Database
import komari.data.DatabaseHandler
import komari.domain.SplashState
import komari.domain.storage.StorageManager

fun appModule(app: Application) = module {
    single<Application> { app }
    single<Context> { app }

    single<SupportSQLiteOpenHelper> {
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(app)
            .callback(DbOpenCallback())
            .name(DbOpenCallback.DATABASE_NAME)
            .noBackupDirectory(false)
            .build()

        /*
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Support database inspector in Android Studio
            FrameworkSQLiteOpenHelperFactory().create(configuration)
        } else {
            RequerySQLiteOpenHelperFactory().create(configuration)
        }
         */
        RequerySQLiteOpenHelperFactory().create(configuration)
    }

    single<SqlDriver> {
        AndroidSqliteDriver(openHelper = get())
        /*
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = app,
            name = "tachiyomi.db",
            factory = if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Support database inspector in Android Studio
                FrameworkSQLiteOpenHelperFactory()
            } else {
                RequerySQLiteOpenHelperFactory()
            },
            callback = get<DbOpenCallback>(),
        )
         */
    }

    single {
        Database(
            driver = get(),
        )
    } withOptions {
        createdAtStart()
    }

    single<DatabaseHandler> { AndroidDatabaseHandler(get(), get()) }

    single { DatabaseHelper(app, get()) } withOptions {
        createdAtStart()
    }

    single { ChapterCache(app) }

    single { CoverCache(app) }

    single {
        NetworkHelper(
            app,
            get(),
        ) { builder ->
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(
                    ChuckerInterceptor.Builder(app)
                        .collector(ChuckerCollector(app))
                        .maxContentLength(250000L)
                        .redactHeaders(emptySet())
                        .alwaysReadResponseBody(false)
                        .build(),
                )
            }
        }
    } withOptions {
        createdAtStart()
    }

    single { JavaScriptEngine(app) }

    single { SourceManager(app, get()) } withOptions {
        createdAtStart()
    }
    single { ExtensionManager(app) }

    single { DownloadManager(app) } withOptions {
        createdAtStart()
    }

    single { CustomMangaManager(app) } withOptions {
        createdAtStart()
    }

    single { TrackManager(app) }

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
    single {
        XML {
            defaultPolicy {
                ignoreUnknownChildren()
            }
            autoPolymorphic = true
            xmlDeclMode = XmlDeclMode.Charset
            indent = 2
            xmlVersion = XmlVersion.XML10
        }
    }

    single { ChapterFilter() }

    single { MangaShortcutManager() }

    single { AndroidStorageFolderProvider(app) }
    single { StorageManager(app, get()) }

    single { SplashState() }
}
