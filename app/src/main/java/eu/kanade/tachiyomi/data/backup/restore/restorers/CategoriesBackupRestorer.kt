package eu.kanade.tachiyomi.data.backup.restore.restorers

import eu.kanade.tachiyomi.data.backup.models.BackupCategory
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import komari.domain.category.interactor.GetCategories

class CategoriesBackupRestorer(
    private val db: DatabaseHelper = Injekt.get(),
    private val getCategories: GetCategories = Injekt.get(),
) {
    suspend fun restoreCategories(backupCategories: List<BackupCategory>, onComplete: () -> Unit) {
        // Get categories from file and from db
        // Do it outside of transaction because StorIO might hang because we're using SQLDelight
        val dbCategories = getCategories.await()
        db.inTransaction {
            // Iterate over them
            backupCategories.map { it.getCategoryImpl() }.forEach { category ->
                // Used to know if the category is already in the db
                var found = false
                for (dbCategory in dbCategories) {
                    // If the category is already in the db, assign the id to the file's category
                    // and do nothing
                    if (category.name == dbCategory.name) {
                        category.id = dbCategory.id
                        found = true
                        break
                    }
                }
                // If the category isn't in the db, remove the id and insert a new category
                // Store the inserted id in the category
                if (!found) {
                    // Let the db assign the id
                    category.id = null
                    val result = db.insertCategory(category).executeAsBlocking()
                    category.id = result.insertedId()?.toInt()
                }
            }
        }

        onComplete()
    }
}
