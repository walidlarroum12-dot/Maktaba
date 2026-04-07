package com.ElOuedUniv.maktaba.domain.usecase

import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.data.repository.BookRepository

/**
 * Use Case for adding a new book
 * This follows the Clean Architecture principle of separating business logic
 *
 * Each Use Case has a single responsibility: in this case, adding a book
 * to the repository.
 */
class AddBookUseCase(
    private val bookRepository: BookRepository
) {
    /**
     * Execute the use case to add a new book
     * @param book The book to add to the repository
     */
    suspend operator fun invoke(book: Book) {
        bookRepository.addBook(book)
    }
}
