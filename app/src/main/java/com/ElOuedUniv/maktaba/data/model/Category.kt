package com.ElOuedUniv.maktaba.data.model

/**
 * Category data model
 * Represents a book category in the library
 *
 * @property id Unique identifier for the category (e.g., "1", "2", "3")
 * @property name Category name (e.g., "Fiction", "Science", "History")
 * @property description Brief description of the category
 */
data class Category(
    val id: String,
    val name: String,
    val description: String
)
