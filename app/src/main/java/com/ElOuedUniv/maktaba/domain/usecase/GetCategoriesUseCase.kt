package com.ElOuedUniv.maktaba.domain.usecase

import com.ElOuedUniv.maktaba.data.model.Category
import com.ElOuedUniv.maktaba.data.repository.CategoryRepository

/**
 * Use Case for getting categories
 * This follows the Clean Architecture principle of separating business logic
 *
 * Use Cases contain the business logic of the application and are independent
 * of the UI and data sources.
 *
 * The operator fun invoke() allows calling the use case like a function:
 *   val categories = getCategoriesUseCase()
 *
 * This layer allows adding business logic later (e.g., sorting, filtering)
 */
class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    /**
     * Execute the use case to get all categories
     * @return List of all categories from the repository
     */
    operator fun invoke(): List<Category> {
        return categoryRepository.getAllCategories()
    }
}
