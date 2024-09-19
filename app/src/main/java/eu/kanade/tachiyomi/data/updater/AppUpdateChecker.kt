package eu.kanade.tachiyomi.data.updater

import android.content.Context
import android.os.Build
import androidx.annotation.VisibleForTesting
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.network.await
import eu.kanade.tachiyomi.network.parseAs
import eu.kanade.tachiyomi.util.system.localeContext
import eu.kanade.tachiyomi.util.system.withIOContext
import kotlinx.serialization.json.Json
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import Komari.domain.base.models.Version
import java.util.*
import java.util.concurrent.*

class AppUpdateChecker(
    private val json: Json = Injekt.get(),
    private val networkService: NetworkHelper = Injekt.get(),
    private val preferences: PreferencesHelper = Injekt.get(),
) {

    suspend fun checkForUpdate(context: Context, isUserPrompt: Boolean = false, doExtrasAfterNewUpdate: Boolean = true): AppUpdateResult {
        // Limit checks to once a day at most
        if (!isUserPrompt && Date().time < preferences.lastAppCheck().get() + TimeUnit.DAYS.toMillis(1)) {
            return AppUpdateResult.NoNewUpdate
        }

        return withIOContext {
            val result = with(json) {
                if (preferences.checkForBetas().get()) {
                    networkService.client
                        .newCall(GET("https://api.github.com/repos/$GITHUB_REPO/releases"))
                        .await()
                        .parseAs<List<GithubRelease>>()
                        .let { githubReleases ->
                            val releases =
                                githubReleases.take(10).filter { isNewVersion(it.version) }
                            // Check if any of the latest versions are newer than the current version
                            val release = releases
                                .maxWithOrNull { r1, r2 ->
                                    when {
                                        r1.version == r2.version -> 0
                                        isNewVersion(r2.version, r1.version) -> -1
                                        else -> 1
                                    }
                                }
                            preferences.lastAppCheck().set(Date().time)

                            if (release != null) {
                                AppUpdateResult.NewUpdate(release)
                            } else {
                                AppUpdateResult.NoNewUpdate
                            }
                        }
                } else {
                    networkService.client
                        .newCall(GET("https://api.github.com/repos/$GITHUB_REPO/releases/latest"))
                        .await()
                        .parseAs<GithubRelease>()
                        .let {
                            preferences.lastAppCheck().set(Date().time)

                            // Check if latest version is newer than the current version
                            if (isNewVersion(it.version)) {
                                AppUpdateResult.NewUpdate(it)
                            } else {
                                AppUpdateResult.NoNewUpdate
                            }
                        }
                }
            }
            if (doExtrasAfterNewUpdate && result is AppUpdateResult.NewUpdate) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    preferences.appShouldAutoUpdate().get() != AppDownloadInstallJob.NEVER
                ) {
                    AppDownloadInstallJob.start(context, null, false, waitUntilIdle = true)
                }
                AppUpdateNotifier(context.localeContext).promptUpdate(result.release)
            }

            result
        }
    }

    @VisibleForTesting
    fun isNewVersion(newVersion: String, currentVersion: String = BuildConfig.VERSION_NAME): Boolean =
        try {
            Version.parse(newVersion) > Version.parse(currentVersion)
        } catch (e: IllegalArgumentException) {
            false
        }
}

val RELEASE_TAG: String by lazy {
    if (BuildConfig.NIGHTLY) {
        "r${BuildConfig.COMMIT_COUNT}"
    } else {
        "v${BuildConfig.VERSION_NAME}"
    }
}

val GITHUB_REPO: String by lazy {
    if (BuildConfig.NIGHTLY) {
        "bigbabyboost/Komari-nightly"
    } else {
        "bigbabyboost/Komari"
    }
}

val RELEASE_URL = "https://github.com/$GITHUB_REPO/releases/tag/$RELEASE_TAG"
