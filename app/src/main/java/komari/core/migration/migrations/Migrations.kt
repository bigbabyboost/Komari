package komari.core.migration.migrations

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import komari.core.migration.Migration

val migrations: ImmutableList<Migration> = persistentListOf(
    // Always run
    SetupAppUpdateMigration(),
    SetupBackupCreateMigration(),
    SetupExtensionUpdateMigration(),
    SetupLibraryUpdateMigration(),

    // Komari fork
    CutoutMigration(),
    ExtensionInstallerEnumMigration(),
    RepoJsonMigration(),
    ThePurgeMigration(),
)
