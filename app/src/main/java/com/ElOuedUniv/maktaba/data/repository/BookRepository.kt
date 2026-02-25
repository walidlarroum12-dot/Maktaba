package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book

/**
 * Repository for managing book data
 * This follows the Repository pattern to abstract data sources
 */
class BookRepository {

    /**
     * TODO for Students (TP1 - Exercise 1):
     * Complete the book information for each book in the list below.
     * Add the following information for each book:
     * - isbn: Use a valid ISBN-13 format (e.g., "978-3-16-148410-0")
     * - nbPages: Add the actual number of pages
     *
     * Example:
     * Book(
     *     isbn = "978-0-13-468599-1",
     *     title = "Clean Code",
     *     nbPages = 464
     * )
     */
    private val booksList = listOf(
        // --- Original 5 books (Exercise 1) ---
        Book(isbn = "978-0-13-235088-4", title = "Clean Code", nbPages = 464),
        Book(isbn = "978-0-13-595705-9", title = "The Pragmatic Programmer", nbPages = 352),
        Book(isbn = "978-0-201-63361-0", title = "Design Patterns", nbPages = 416),
        Book(isbn = "978-0-13-475759-9", title = "Refactoring", nbPages = 448),
        Book(isbn = "978-0-596-00712-6", title = "Head First Design Patterns", nbPages = 692),

        // --- 5 new books (Exercise 2) ---
        Book(isbn = "978-0-262-04630-5", title = "Introduction to Algorithms", nbPages = 1312),
        Book(isbn = "978-1-617-29329-0", title = "Kotlin in Action", nbPages = 360),
        Book(isbn = "978-0-13-359414-0", title = "Computer Networking: A Top-Down Approach", nbPages = 864),
        Book(isbn = "978-1-119-80036-1", title = "Operating System Concepts", nbPages = 1040),
        Book(isbn = "978-0-078-02215-9", title = "Database System Concepts", nbPages = 1376)
    )

    /**
     * TODO for Students (TP1 - Exercise 2):
     * Add 5 more books to the list above.
     * Choose books related to Computer Science, Programming, or any topic you like.
     * Remember to include complete information (ISBN, title, nbPages).
     *
     * Tip: You can find ISBN numbers for books on:
     * - Google Books
     * - Amazon
     * - GoodReads
     */

    /**
     * Get all books from the repository
     * @return List of all books
     */
    fun getAllBooks(): List<Book> {
        return booksList
    }

    /**
     * Get a book by ISBN
     * @param isbn The ISBN of the book to find
     * @return The book if found, null otherwise
     */
    fun getBookByIsbn(isbn: String): Book? {
        return booksList.find { it.isbn == isbn }
    }

    /**
     * Bonus 3: Get only books with more than 400 pages
     * @return List of books with nbPages > 400
     */
    fun getLongBooks(): List<Book> {
        return booksList.filter { it.nbPages > 400 }
    }
}
