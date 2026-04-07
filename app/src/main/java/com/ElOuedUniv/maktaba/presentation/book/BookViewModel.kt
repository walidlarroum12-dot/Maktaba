package com.ElOuedUniv.maktaba.presentation.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.domain.usecase.AddBookUseCase
import com.ElOuedUniv.maktaba.domain.usecase.GetBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing book-related UI state
 * This follows the MVVM pattern where ViewModel acts as a bridge between
 * the UI and the business logic (Use Cases)
 *
 * Key patterns used:
 * - Single UiState object (instead of multiple StateFlows)
 * - Sealed Interface actions (user interactions)
 * - SharedFlow events (one-time system effects)
 *
 * @HiltViewModel tells Hilt that this ViewModel should be injectable.
 * @Inject constructor tells Hilt HOW to create this ViewModel by
 * automatically providing the required Use Cases from DomainModule.
 *
 * @param getBooksUseCase Use case for retrieving books (injected by Hilt)
 * @param addBookUseCase Use case for adding a new book (injected by Hilt)
 */
@HiltViewModel
class BookViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val addBookUseCase: AddBookUseCase
) : ViewModel() {

    /**
     * Single source of truth for the Book screen UI state.
     * Uses MutableStateFlow internally and exposes an immutable StateFlow.
     */
    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    /**
     * One-time events channel using SharedFlow.
     * Unlike StateFlow, SharedFlow does NOT replay the last value,
     * so events are consumed only once (e.g., showing a toast).
     */
    private val _uiEvent = MutableSharedFlow<BookUiEvent>()
    val uiEvent: SharedFlow<BookUiEvent> = _uiEvent.asSharedFlow()

    init {
        // Load books when ViewModel is created
        loadBooks()
    }

    /**
     * Central function to handle all user actions.
     * Uses a when expression to dispatch each action to the appropriate handler.
     *
     * @param action The user action to process
     */
    fun onAction(action: BookUiAction) {
        when (action) {
            is BookUiAction.OnAddBookClick -> {
                // Show the Add Book dialog
                _uiState.update { it.copy(isAddingBook = true) }
            }

            is BookUiAction.OnDismissAddBook -> {
                // Hide the Add Book dialog and reset form fields
                _uiState.update {
                    it.copy(
                        isAddingBook = false,
                        newBookTitle = "",
                        newBookIsbn = "",
                        newBookPages = ""
                    )
                }
            }

            is BookUiAction.OnAddBookConfirm -> {
                // Add the book and close the dialog
                addBook(
                    title = action.title,
                    isbn = action.isbn,
                    nbPages = action.nbPages
                )
            }

            is BookUiAction.OnRefresh -> {
                loadBooks()
            }
        }
    }

    /**
     * Load all books from the use case.
     * Updates the UiState using .copy() to set isLoading, books, and totalPages.
     */
    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bookList = getBooksUseCase()
                _uiState.update {
                    it.copy(
                        books = bookList,
                        isLoading = false,
                        totalPages = bookList.sumOf { book -> book.nbPages }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.emit(BookUiEvent.ShowToast("Failed to load books: ${e.message}"))
            }
        }
    }

    /**
     * Add a new book via the AddBookUseCase.
     * On success, reloads the list and emits a BookAdded event.
     * On failure, emits a ShowToast event with the error.
     */
    private fun addBook(title: String, isbn: String, nbPages: Int) {
        viewModelScope.launch {
            try {
                val newBook = Book(
                    isbn = isbn,
                    title = title,
                    nbPages = nbPages
                )
                addBookUseCase(newBook)

                // Close dialog and reset fields
                _uiState.update {
                    it.copy(
                        isAddingBook = false,
                        newBookTitle = "",
                        newBookIsbn = "",
                        newBookPages = ""
                    )
                }

                // Reload books to get the updated list
                loadBooks()

                // Emit one-time event
                _uiEvent.emit(BookUiEvent.BookAdded(title))

            } catch (e: Exception) {
                _uiEvent.emit(BookUiEvent.ShowToast("Failed to add book: ${e.message}"))
            }
        }
    }
}
