package komari.core.migration.migrations

import android.app.Application
import eu.kanade.tachiyomi.extension.ExtensionUpdateJob
import komari.core.migration.Migration
import komari.core.migration.MigrationContext

class SetupExtensionUpdateMigration : Migration {
    override val version: Float = Migration.ALWAYS

    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val context = migrationContext.get<Application>() ?: return false
        ExtensionUpdateJob.setupTask(context)
        return true
    }
}
