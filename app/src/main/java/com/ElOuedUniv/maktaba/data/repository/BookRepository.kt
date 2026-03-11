package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book

/**
 * Repository interface for managing book data
 * Defines the contract that any book data source must implement
 *
 * Benefits of using an interface:
 * - Abstraction: Separate contract from implementation
 * - Testability: Easy to create mock implementations
 * - Flexibility: Swap implementations (e.g., in-memory → database)
 * - SOLID: Follows Dependency Inversion Principle
 */
interface BookRepository {

    /**
     * Get all books from the repository
     * @return List of all books
     */
    fun getAllBooks(): List<Book>

    /**
     * Get a book by ISBN
     * @param isbn The ISBN of the book to find
     * @return The book if found, null otherwise
     */
    fun getBookByIsbn(isbn: String): Book?

    /**
     * Get only books with more than 400 pages
     * @return List of books with nbPages > 400
     */
    fun getLongBooks(): List<Book>
}
