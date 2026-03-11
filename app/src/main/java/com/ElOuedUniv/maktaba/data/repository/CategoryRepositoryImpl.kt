package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Category

/**
 * Implementation of CategoryRepository
 * Provides in-memory category data for the library
 *
 * This class implements the CategoryRepository interface,
 * following the Repository Pattern to abstract the data source.
 */
class CategoryRepositoryImpl : CategoryRepository {

    /**
     * In-memory list of categories
     * In a real app, this data would come from a database or API
     */
    private val categoriesList = listOf(
        Category(
            id = "1",
            name = "Programming",
            description = "Books about software development and coding"
        ),
        Category(
            id = "2",
            name = "Algorithms",
            description = "Books about algorithms and data structures"
        ),
        Category(
            id = "3",
            name = "Databases",
            description = "Books about database design and management"
        ),
        Category(
            id = "4",
            name = "Computer Networks",
            description = "Books about networking protocols and architecture"
        ),
        Category(
            id = "5",
            name = "Operating Systems",
            description = "Books about OS concepts and system programming"
        )
    )

    /**
     * Get all categories from the repository
     * @return List of all categories
     */
    override fun getAllCategories(): List<Category> {
        return categoriesList
    }

    /**
     * Get a category by its ID
     * Uses .find { } to search the list for a matching element
     *
     * @param id The unique identifier of the category to find
     * @return The first matching category, or null if not found
     */
    override fun getCategoryById(id: String): Category? {
        return categoriesList.find { it.id == id }
    }
}
