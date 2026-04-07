package com.ElOuedUniv.maktaba.presentation.book

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ElOuedUniv.maktaba.data.model.Book

/**
 * Main screen displaying the list of books
 * Now uses the unified UiState pattern and dispatches Actions to the ViewModel.
 *
 * The viewModel is provided by Hilt via hiltViewModel() — no need to pass it manually!
 * hiltViewModel() automatically creates and scopes the ViewModel to the composable's lifecycle.
 *
 * @param viewModel The BookViewModel, automatically injected by Hilt via hiltViewModel()
 * @param onCategoriesClick Callback for navigating to the categories screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListView(
    viewModel: BookViewModel = hiltViewModel(),
    onCategoriesClick: () -> Unit
) {
    // Collect the single UiState (Exercise 1)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Collect one-time UI events (Exercise 3)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is BookUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is BookUiEvent.BookAdded -> {
                    Toast.makeText(
                        context,
                        "Book \"${event.bookTitle}\" added successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is BookUiEvent.NavigateBack -> {
                    // Handle navigation if needed
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maktaba - My Library") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onCategoriesClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Categories"
                        )
                    }
                }
            )
        },
        // Floating Action Button for adding a new book (Exercise 3)
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Dispatch the OnAddBookClick action to the ViewModel
                    viewModel.onAction(BookUiAction.OnAddBookClick)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Book"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                if (uiState.books.isEmpty()) {
                    EmptyBooksMessage(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    BookList(
                        books = uiState.books,
                        totalPages = uiState.totalPages,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Exercise 4: Show AddBookDialog when isAddingBook is true
        if (uiState.isAddingBook) {
            AddBookDialog(
                onDismiss = {
                    viewModel.onAction(BookUiAction.OnDismissAddBook)
                },
                onConfirm = { title, isbn, nbPages ->
                    viewModel.onAction(
                        BookUiAction.OnAddBookConfirm(
                            title = title,
                            isbn = isbn,
                            nbPages = nbPages
                        )
                    )
                }
            )
        }
    }
}

/**
 * Composable for displaying a list of books with a stats header
 */
@Composable
fun BookList(
    books: List<Book>,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bonus 1 & 2: Stats summary at the top
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📚",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total Books: ${books.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📄",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total Pages: $totalPages",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        // Book items
        items(books) { book ->
            BookItem(book = book)
        }
    }
}

/**
 * Composable for displaying a single book item
 */
@Composable
fun BookItem(book: Book) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ISBN:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (book.isbn.isEmpty()) "Not set" else book.isbn,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Pages:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (book.nbPages == 0) "Not set" else "${book.nbPages}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Composable for displaying empty state message
 */
@Composable
fun EmptyBooksMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📚",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No books in your library",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to add a new book",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
