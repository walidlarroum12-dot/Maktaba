package com.ElOuedUniv.maktaba.presentation.book

import com.ElOuedUniv.maktaba.data.model.Book

/**
 * UI State for the Book screen
 * Consolidates all UI-related state into a single data class
 *
 * Instead of having multiple MutableStateFlow variables (like _books, _isLoading),
 * we use a single UiState object. This is cleaner and easier to manage.
 *
 * @property books The list of books to display
 * @property isLoading Whether data is currently being loaded
 * @property totalPages Total number of pages across all books
 * @property isAddingBook Whether the Add Book dialog should be shown
 * @property newBookTitle Title input for the new book being added
 * @property newBookIsbn ISBN input for the new book being added
 * @property newBookPages Pages input for the new book being added
 */
data class BookUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val totalPages: Int = 0,
    val isAddingBook: Boolean = false,
    val newBookTitle: String = "",
    val newBookIsbn: String = "",
    val newBookPages: String = ""
)
