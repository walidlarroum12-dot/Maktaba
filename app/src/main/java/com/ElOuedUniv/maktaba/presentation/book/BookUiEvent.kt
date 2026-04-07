package com.ElOuedUniv.maktaba.presentation.book

/**
 * Sealed Interface for Book UI Events
 * Events represent things the SYSTEM tells the UI to do (show toast, navigate, show snackbar).
 *
 * Unlike UiState (which is persistent), events are one-time effects that are consumed once.
 * They are emitted via SharedFlow to ensure they are not replayed on configuration changes.
 */
sealed interface BookUiEvent {

    /**
     * Show a toast/snackbar message to the user
     * @property message The message to display
     */
    data class ShowToast(val message: String) : BookUiEvent

    /** Navigate back to the previous screen */
    data object NavigateBack : BookUiEvent

    /**
     * Book was successfully added
     * @property bookTitle The title of the book that was added
     */
    data class BookAdded(val bookTitle: String) : BookUiEvent
}
