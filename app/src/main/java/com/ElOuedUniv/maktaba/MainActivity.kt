package com.ElOuedUniv.maktaba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ElOuedUniv.maktaba.presentation.book.BookListView
import com.ElOuedUniv.maktaba.presentation.screens.CategoryListScreen
import com.ElOuedUniv.maktaba.presentation.theme.MaktabaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity - Entry point of the application
 *
 * @AndroidEntryPoint tells Hilt that this Activity participates in DI.
 * Hilt will generate the code needed to inject dependencies into this Activity.
 *
 * Notice how we NO LONGER manually create repositories, use cases, and ViewModels!
 * Hilt handles all of that automatically through:
 * - DataModule → provides repositories
 * - DomainModule → provides use cases
 * - @HiltViewModel → provides ViewModels
 * - hiltViewModel() → retrieves ViewModels in Composables
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ No more manual dependency creation!
        // Before (TP2): val bookRepository = BookRepositoryImpl()
        //               val getBooksUseCase = GetBooksUseCase(bookRepository)
        //               val bookViewModel = BookViewModel(getBooksUseCase)
        // Now: Hilt does all of this automatically!

        setContent {
            MaktabaTheme {
                var showCategories by remember { mutableStateOf(false) }

                if (showCategories) {
                    // hiltViewModel() is called inside CategoryListScreen by default
                    CategoryListScreen(
                        onBackClick = { showCategories = false }
                    )
                } else {
                    // hiltViewModel() is called inside BookListView by default
                    BookListView(
                        onCategoriesClick = { showCategories = true }
                    )
                }
            }
        }
    }
}