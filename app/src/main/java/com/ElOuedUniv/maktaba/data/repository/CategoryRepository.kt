package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Category

/**
 * Repository interface for managing category data
 * Defines the contract that any category data source must implement
 *
 * Benefits of using an interface:
 * - Abstraction: Separate contract from implementation
 * - Testability: Easy to create mock implementations
 * - Flexibility: Swap implementations (e.g., in-memory → database)
 * - SOLID: Follows Dependency Inversion Principle
 */
interface CategoryRepository {

    /**
     * Get all categories from the repository
     * @return List of all categories
     */
    fun getAllCategories(): List<Category>

    /**
     * Get a category by its ID
     * @param id The unique identifier of the category to find
     * @return The category if found, null otherwise
     */
    fun getCategoryById(id: String): Category?
}
