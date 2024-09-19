package komari.domain.category

import eu.kanade.tachiyomi.data.database.models.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getAll(): List<Category>
    fun getAllAsFlow(): Flow<List<Category>>
}
