package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

/**
 * Implementation of BookRepository
 * Provides in-memory book data for the library
 *
 * This class implements the BookRepository interface,
 * following the Repository Pattern to abstract the data source.
 *
 * Uses MutableSharedFlow to notify observers when the book list changes
 * (e.g., after a new book is added).
 */
class BookRepositoryImpl : BookRepository {

    /**
     * In-memory mutable list of books
     * In a real app, this data would come from a database or API
     */
    private val booksList = mutableListOf(
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
     * SharedFlow used to signal that the book list has changed.
     * When a book is added, we emit the updated list through this flow.
     */
    private val _booksFlow = MutableSharedFlow<List<Book>>(replay = 1)

    /**
     * Get all books from the repository (synchronous)
     * @return List of all books
     */
    override fun getAllBooks(): List<Book> {
        return booksList.toList()
    }

    /**
     * Observe the list of books reactively.
     * Starts by emitting the current list, then emits updates whenever a book is added.
     * @return Flow of the book list
     */
    override fun getBooks(): Flow<List<Book>> {
        return _booksFlow.onStart { emit(booksList.toList()) }
    }

    /**
     * Add a new book to the repository.
     * After adding, emits the updated list through the SharedFlow
     * so that all observers (e.g., ViewModel) are notified.
     *
     * @param book The book to add
     */
    override suspend fun addBook(book: Book) {
        booksList.add(book)
        _booksFlow.emit(booksList.toList())
    }

    /**
     * Get a book by ISBN
     * @param isbn The ISBN of the book to find
     * @return The book if found, null otherwise
     */
    override fun getBookByIsbn(isbn: String): Book? {
        return booksList.find { it.isbn == isbn }
    }

    /**
     * Bonus 3: Get only books with more than 400 pages
     * @return List of books with nbPages > 400
     */
    override fun getLongBooks(): List<Book> {
        return booksList.filter { it.nbPages > 400 }
    }
}
