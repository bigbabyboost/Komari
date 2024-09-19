package komari.core.migration.migrations

import android.app.Application
import androidx.preference.PreferenceManager
import eu.kanade.tachiyomi.data.preference.PreferenceKeys
import komari.core.migration.Migration
import komari.core.migration.MigrationContext
import komari.domain.ui.settings.ReaderPreferences
import komari.domain.ui.settings.ReaderPreferences.CutoutBehaviour
import komari.domain.ui.settings.ReaderPreferences.LandscapeCutoutBehaviour

class CutoutMigration : Migration {
    override val version: Float = 121f

    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val readerPreferences = migrationContext.get<ReaderPreferences>() ?: return false
        val context = migrationContext.get<Application>() ?: return false
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        try {
            val oldCutoutBehaviour = prefs.getInt(PreferenceKeys.pagerCutoutBehavior, 0)
            readerPreferences.pagerCutoutBehavior().set(CutoutBehaviour.migrate(oldCutoutBehaviour))
        } catch (_: Exception) {
        }

        try {
            val oldCutoutBehaviour = prefs.getInt("landscape_cutout_behavior", 0)
            readerPreferences.landscapeCutoutBehavior().set(LandscapeCutoutBehaviour.migrate(oldCutoutBehaviour))
        } catch (_: Exception) {
        }
        return true
    }
}
