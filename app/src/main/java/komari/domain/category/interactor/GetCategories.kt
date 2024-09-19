package komari.domain.category.interactor

import komari.domain.category.CategoryRepository

class GetCategories(
    private val categoryRepository: CategoryRepository,
) {
    suspend fun await() = categoryRepository.getAll()
    fun subscribe() = categoryRepository.getAllAsFlow()
}
