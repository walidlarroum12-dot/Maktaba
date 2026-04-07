package com.ElOuedUniv.maktaba.presentation.book

/**
 * Sealed Interface for Book UI Actions
 * Actions represent things the USER does (clicks, types, scrolls).
 *
 * Each action is a data object or data class that describes a user interaction.
 * The ViewModel receives these actions and processes them accordingly.
 */
sealed interface BookUiAction {

    /** User clicked the FAB to add a new book */
    data object OnAddBookClick : BookUiAction

    /** User dismissed the Add Book dialog */
    data object OnDismissAddBook : BookUiAction

    /**
     * User confirmed adding a new book
     * @property title The title of the new book
     * @property isbn The ISBN of the new book
     * @property nbPages The number of pages of the new book
     */
    data class OnAddBookConfirm(
        val title: String,
        val isbn: String,
        val nbPages: Int
    ) : BookUiAction

    /** User triggered a refresh of the book list */
    data object OnRefresh : BookUiAction
}
