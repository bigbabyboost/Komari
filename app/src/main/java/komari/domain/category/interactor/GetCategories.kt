package Komari.domain.category.interactor

import Komari.domain.category.CategoryRepository

class GetCategories(
    private val categoryRepository: CategoryRepository,
) {
    suspend fun await() = categoryRepository.getAll()
    fun subscribe() = categoryRepository.getAllAsFlow()
}
