package com.ElOuedUniv.maktaba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Category
import com.ElOuedUniv.maktaba.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing category-related UI state
 * This follows the MVVM pattern where ViewModel acts as a bridge between
 * the UI and the business logic (Use Cases)
 *
 * @HiltViewModel tells Hilt that this ViewModel should be injectable.
 * @Inject constructor tells Hilt HOW to create this ViewModel by
 * automatically providing the required Use Cases from DomainModule.
 *
 * @param getCategoriesUseCase Use case for retrieving categories (injected by Hilt)
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // Private mutable state for internal use
    private val _categories = MutableStateFlow<List<Category>>(emptyList())

    // Public immutable state for UI observation
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Filtered categories based on search query
     * When query is empty, shows all categories
     * When query has a value, filters by ID
     */
    val filteredCategories: StateFlow<List<Category>> = combine(
        _categories,
        _searchQuery
    ) { categoryList, query ->
        if (query.isBlank()) {
            categoryList
        } else {
            categoryList.filter { it.id == query.trim() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Load categories when ViewModel is created
        loadCategories()
    }

    /**
     * Load all categories from the use case
     */
    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val categoryList = getCategoriesUseCase()
                _categories.value = categoryList
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update the search query for filtering categories by ID
     * @param query The search query (category ID)
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Refresh the categories list
     * Can be called from UI to reload data
     */
    fun refreshCategories() {
        loadCategories()
    }

    /**
     * Bonus 2: Search category by ID
     * Finds a category in the current list by its unique identifier
     *
     * @param id The unique identifier of the category to find
     * @return The category if found, null otherwise
     */
    fun getCategoryById(id: String): Category? {
        return categories.value.find { it.id == id }
    }
}
