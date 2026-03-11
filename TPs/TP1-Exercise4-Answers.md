# TP1 - Exercise 4: Code Analysis Answers

**Student:** *(write your name here)*  
**Date:** 2026-02-25  
**Course:** Mobile Application Development — El Oued University

---

## 4.1 Book Model (`Book.kt`)

### Q1: What is a `data class` in Kotlin?

A `data class` is a special type of class in Kotlin designed **specifically to hold data**. Unlike a regular class, the Kotlin compiler automatically generates the following functions for you:

| Generated Function | Purpose |
|---|---|
| `equals()` | Compares two objects by their **property values**, not by reference |
| `hashCode()` | Generates a hash based on property values (needed for collections like `HashMap`) |
| `toString()` | Returns a readable string like `Book(isbn=978-..., title=Clean Code, nbPages=464)` |
| `copy()` | Creates a **shallow copy** of the object with the option to change some properties |
| `componentN()` | Enables destructuring: `val (isbn, title, pages) = book` |

**Example:**

```kotlin
// With data class — all functions are auto-generated
data class Book(
    val isbn: String,
    val title: String,
    val nbPages: Int
)

// Without data class — you would need to write all of this manually:
class Book(
    val isbn: String,
    val title: String,
    val nbPages: Int
) {
    override fun equals(other: Any?): Boolean { /* ... */ }
    override fun hashCode(): Int { /* ... */ }
    override fun toString(): String { /* ... */ }
    fun copy(...): Book { /* ... */ }
}
```

### Q2: Why do we use `val` instead of `var` for the properties?

We use `val` (value — read-only) instead of `var` (variable — mutable) to make the `Book` object **immutable**. This means once a `Book` is created, its properties **cannot be changed**.

**Benefits of immutability:**

1. **Thread safety** — Immutable objects can be safely shared between multiple threads (coroutines) without synchronization issues.
2. **Predictability** — No one can accidentally modify a book's ISBN or title after creation. The data stays consistent.
3. **Easier debugging** — If data never changes, you don't need to track down where/when it was modified.
4. **Works well with StateFlow/Compose** — Jetpack Compose relies on immutable state to detect changes and recompose the UI. If objects were mutable, Compose might miss updates.

### Q3: What would happen if we used `var` instead?

If we used `var`, the properties would become **mutable** — any code with a reference to the `Book` object could change its values:

```kotlin
data class Book(
    var isbn: String,    // ⚠️ Mutable
    var title: String,   // ⚠️ Mutable
    var nbPages: Int     // ⚠️ Mutable
)

val book = Book(isbn = "978-0-13-235088-4", title = "Clean Code", nbPages = 464)
book.title = "Dirty Code"  // 😱 This would be allowed!
book.nbPages = -5           // 😱 Invalid data — no protection!
```

**Problems this causes:**

- **Unexpected side effects** — One part of the app could change the book object, and another part wouldn't know about it.
- **Jetpack Compose issues** — Compose detects state changes by comparing object references. Mutating an existing object **does not trigger recomposition**, so the UI would show stale data.
- **Broken `hashCode`** — If a book is stored in a `HashSet` or `HashMap` and its properties change, the hash changes too, making the object impossible to find again.

---

## 4.2 Repository (`BookRepository.kt`)

### Q1: What is the purpose of the `BookRepository` class?

The `BookRepository` class serves as an **abstraction layer** between the data sources and the rest of the application. Its purpose is to:

1. **Centralize data access** — All data operations go through one place, making it easy to manage.
2. **Abstract the data source** — The ViewModel and Use Cases don't need to know *where* the data comes from (local list, database, API, etc.). They just call `getAllBooks()`.
3. **Enable easy swapping** — If we later want to fetch books from a **Room database** or a **REST API** instead of a hardcoded list, we only change the Repository — nothing else.
4. **Single source of truth** — The repository decides which data source to use and ensures consistency.

```
ViewModel → UseCase → Repository → [Local List / Database / API]
                                      ↑ Only this part changes
```

### Q2: What does the `getAllBooks()` function return?

The `getAllBooks()` function returns `List<Book>` — an **immutable list** containing all `Book` objects stored in the private `booksList` property.

```kotlin
fun getAllBooks(): List<Book> {
    return booksList  // Returns the full list of 10 books
}
```

- Return type: `List<Book>` (Kotlin's `List` is read-only by default)
- The caller **cannot add or remove** books from the returned list
- Currently returns 10 books (5 from Exercise 1 + 5 from Exercise 2)

### Q3: How would you add a function to search books by title?

You would add a new function to the `BookRepository` class that uses Kotlin's `filter` function to search through the list:

```kotlin
fun searchBooksByTitle(query: String): List<Book> {
    return booksList.filter { book ->
        book.title.contains(query, ignoreCase = true)
    }
}
```

**How it works:**

1. `.filter { }` iterates over every book in `booksList`
2. For each book, it checks if `book.title` **contains** the `query` string
3. `ignoreCase = true` makes it case-insensitive (so "clean" matches "Clean Code")
4. Only books that match are included in the returned list

### Challenge: `searchBooksByTitle` function

```kotlin
/**
 * Search for books by title
 * @param query The search query string
 * @return List of books whose title contains the query (case-insensitive)
 */
fun searchBooksByTitle(query: String): List<Book> {
    if (query.isBlank()) return booksList  // Return all books if query is empty
    return booksList.filter { book ->
        book.title.contains(query, ignoreCase = true)
    }
}
```

**Usage examples:**

```kotlin
val repo = BookRepository()

repo.searchBooksByTitle("Design")
// Returns: [Design Patterns, Head First Design Patterns]

repo.searchBooksByTitle("code")
// Returns: [Clean Code] (case-insensitive!)

repo.searchBooksByTitle("algorithms")
// Returns: [Introduction to Algorithms]

repo.searchBooksByTitle("")
// Returns: all 10 books
```

---

## 4.3 Use Case (`GetBooksUseCase.kt`)

### Q1: Why do we have a separate Use Case instead of calling the repository directly from the ViewModel?

Having a separate Use Case follows the **Clean Architecture** principle of **Separation of Concerns**. There are several important reasons:

1. **Single Responsibility** — Each Use Case does **one thing only**. `GetBooksUseCase` only gets books. If we need to add books, we create `AddBookUseCase`.

2. **Business logic isolation** — Business rules (e.g., "only show books with more than 300 pages", "sort alphabetically") live in the Use Case, **not** in the ViewModel or Repository.

3. **Reusability** — The same Use Case can be called from multiple ViewModels. For example, both `BookListViewModel` and `SearchViewModel` could use `GetBooksUseCase`.

4. **Testability** — Use Cases are easy to unit test because they have no Android dependencies (no Context, no lifecycle).

5. **ViewModel stays lean** — The ViewModel only manages UI state. It doesn't need to know the business rules or how data is fetched.

```
Without Use Case:           With Use Case:
ViewModel → Repository      ViewModel → UseCase → Repository
(business logic mixed       (clean separation)
 into the ViewModel)
```

### Q2: What is the `operator fun invoke()` syntax?

The `operator fun invoke()` is a **Kotlin operator overload** that allows you to call an object instance **as if it were a function**. When you define `operator fun invoke()`, you can use the object with parentheses `()`:

```kotlin
class GetBooksUseCase(private val bookRepository: BookRepository) {
    operator fun invoke(): List<Book> {
        return bookRepository.getAllBooks()
    }
}

// Usage — these two lines are EQUIVALENT:
val books = getBooksUseCase.invoke()   // Explicit call
val books = getBooksUseCase()          // Shorthand thanks to operator invoke
```

This syntax makes Use Cases read more naturally in the ViewModel code:

```kotlin
// Reads like calling a function
val bookList = getBooksUseCase()
```

### Q3: How would you add business logic to filter only books with more than 300 pages?

You would modify the `invoke()` function in the Use Case to apply the filtering rule:

```kotlin
class GetBooksUseCase(
    private val bookRepository: BookRepository
) {
    operator fun invoke(minPages: Int = 0): List<Book> {
        val allBooks = bookRepository.getAllBooks()
        return if (minPages > 0) {
            allBooks.filter { it.nbPages > minPages }
        } else {
            allBooks
        }
    }
}

// Usage:
val allBooks = getBooksUseCase()         // Returns all books
val longBooks = getBooksUseCase(300)     // Returns only books with > 300 pages
```

**Important:** The filtering logic belongs in the **Use Case**, not in the Repository (which is only responsible for data access) and not in the ViewModel (which is only responsible for UI state).

---

## 4.4 ViewModel (`BookViewModel.kt`)

### Q1: What is `StateFlow` and why do we use it?

`StateFlow` is a **state-holder observable flow** from Kotlin Coroutines. It always holds a **current value** and **emits updates** whenever that value changes.

**Key characteristics:**

| Feature | Description |
|---|---|
| **Always has a value** | Unlike `Flow`, a `StateFlow` always holds a current value (initialized in constructor) |
| **Hot stream** | It's active regardless of whether there are collectors (observers) |
| **Conflated** | If multiple updates happen quickly, only the **latest** value is delivered |
| **Lifecycle-aware** | When used with `collectAsState()` in Compose, it automatically respects the lifecycle |

**Why we use it in MVVM:**

1. **Reactive UI** — When `_books.value` changes, the Compose UI automatically **recomposes** to show the new data.
2. **Survives configuration changes** — Because the ViewModel outlives the Activity during rotation, the StateFlow keeps its value.
3. **No memory leaks** — Unlike `LiveData` callbacks, `collectAsState()` in Compose automatically stops collecting when the composable leaves the composition.

```kotlin
// In ViewModel:
private val _books = MutableStateFlow<List<Book>>(emptyList())
val books: StateFlow<List<Book>> = _books.asStateFlow()

// In Compose UI:
val books by viewModel.books.collectAsState()  // Auto-updates when value changes
```

### Q2: What is the difference between `_books` (private) and `books` (public)?

This pattern is called **backing property** and it controls **who can modify** the state:

```kotlin
// Private + Mutable — only the ViewModel can change the value
private val _books = MutableStateFlow<List<Book>>(emptyList())

// Public + Read-only — the UI can only READ, never write
val books: StateFlow<List<Book>> = _books.asStateFlow()
```

| Property | Type | Visibility | Can Modify? | Used By |
|---|---|---|---|---|
| `_books` | `MutableStateFlow` | `private` | ✅ Yes | ViewModel only |
| `books` | `StateFlow` | `public` | ❌ No (read-only) | UI (Compose screens) |

**Why this matters:**

- The UI **should never directly modify** the data. It should only observe it.
- All data modifications go through the ViewModel's functions (like `loadBooks()`), ensuring **controlled, predictable state changes**.
- This enforces **unidirectional data flow**: `Data → ViewModel → UI`

```kotlin
// ✅ Inside ViewModel — allowed:
_books.value = newBookList

// ❌ From UI — NOT allowed (won't compile):
viewModel.books.value = hackedList  // Error: books is StateFlow, not MutableStateFlow
```

### Q3: What is `viewModelScope` and why do we use it?

`viewModelScope` is a **CoroutineScope** tied to the ViewModel's lifecycle. It is provided by the `androidx.lifecycle:lifecycle-viewmodel-ktx` library.

**Key features:**

1. **Automatic cancellation** — All coroutines launched in `viewModelScope` are **automatically cancelled** when the ViewModel is destroyed (e.g., when the user navigates away from the screen).
2. **No memory leaks** — Because coroutines are cancelled automatically, there's no risk of background work continuing after the screen is gone.
3. **Main thread by default** — Coroutines in `viewModelScope` run on `Dispatchers.Main` by default, which is safe for updating UI state.

```kotlin
private fun loadBooks() {
    viewModelScope.launch {       // Launches a coroutine
        _isLoading.value = true   // Safe to update UI state (Main thread)
        try {
            val bookList = getBooksUseCase()
            _books.value = bookList
        } finally {
            _isLoading.value = false
        }
    }
    // If ViewModel is destroyed while this runs → coroutine is cancelled automatically
}
```

**Without `viewModelScope`**, you would need to manually manage a `CoroutineScope` and remember to cancel it in `onCleared()` — error-prone and boilerplate-heavy.

### Q4: When is the `init` block executed?

The `init` block is executed **immediately when the ViewModel instance is created** — after the constructor parameters are assigned but before any other function is called.

```kotlin
class BookViewModel(
    private val getBooksUseCase: GetBooksUseCase  // 1️⃣ Constructor parameter assigned
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())  // 2️⃣ Properties initialized
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    init {
        // 3️⃣ init block runs — books are loaded automatically
        loadBooks()
    }

    // 4️⃣ ViewModel is ready to be used by the UI
}
```

**Execution order:**
1. Constructor parameter (`getBooksUseCase`) is assigned
2. Property declarations are initialized (`_books`, `books`, `_isLoading`, `isLoading`)
3. `init { }` block runs → calls `loadBooks()`
4. `loadBooks()` launches a coroutine that fetches books and updates `_books.value`
5. The UI observes the change and displays the books

**In our app**, this means books are loaded **automatically** when `BookViewModel` is created in `MainActivity` — no manual trigger needed.

---

## 4.5 View (`BookListScreen.kt`)

### Q1: How does the screen observe changes in the ViewModel?

The screen uses `collectAsState()` to convert the ViewModel's `StateFlow` into Compose `State`, which triggers **automatic recomposition** when the value changes:

```kotlin
@Composable
fun BookListScreen(viewModel: BookViewModel) {
    val books by viewModel.books.collectAsState()        // Observes book list
    val isLoading by viewModel.isLoading.collectAsState() // Observes loading state
    // ...
}
```

**How it works step by step:**

1. `viewModel.books` is a `StateFlow<List<Book>>` — a reactive stream
2. `.collectAsState()` subscribes to the StateFlow and converts it to a Compose `State<List<Book>>`
3. The `by` keyword delegates the property so `books` directly gives us `List<Book>` (not `State<List<Book>>`)
4. When `_books.value` changes in the ViewModel, `collectAsState()` receives the new value
5. Compose detects the `State` has changed and **recomposes** only the parts of the UI that use `books`

**This is the reactive programming cycle:**

```
ViewModel updates _books.value
        ↓
StateFlow emits new value
        ↓
collectAsState() receives it
        ↓
Compose State changes
        ↓
UI recomposes automatically
```

### Q2: What is `LazyColumn` and why do we use it instead of `Column`?

`LazyColumn` is the Compose equivalent of `RecyclerView` in traditional Android. It is a **lazy scrollable list** that only composes and renders the items that are **currently visible on screen**.

| Feature | `Column` | `LazyColumn` |
|---|---|---|
| **Rendering** | Renders **all** items at once | Renders only **visible** items |
| **Memory** | All items in memory | Only visible items in memory |
| **Performance** | ❌ Slow for large lists | ✅ Fast for any list size |
| **Scrolling** | Not scrollable by default | Scrollable by default |
| **Use case** | Small, fixed layouts | Dynamic lists of any size |

**Why `LazyColumn` is better for our book list:**

```kotlin
// ❌ Column — if we had 1000 books, ALL 1000 would be composed at once
Column {
    books.forEach { book ->
        BookItem(book = book)  // 1000 items in memory!
    }
}

// ✅ LazyColumn — only ~10 visible items are composed at a time
LazyColumn {
    items(books) { book ->
        BookItem(book = book)  // Only visible items in memory
    }
}
```

Even with 10 books the difference is small, but using `LazyColumn` is **best practice** because:
- The list might grow (Exercise 2 already added more books)
- It provides built-in scrolling, padding, and spacing
- It handles item recycling efficiently

### Q3: What happens when the books list is empty?

When the books list is empty, the screen shows the **`EmptyBooksMessage`** composable — a friendly empty state:

```kotlin
if (books.isEmpty()) {
    EmptyBooksMessage(
        modifier = Modifier.align(Alignment.Center)
    )
}
```

The `EmptyBooksMessage` composable displays:

1. A **📚 book emoji** in large display text
2. The text **"No books in your library"** as a medium title
3. The hint **"Complete the TODO exercises in BookRepository.kt"** as body text

```
        ┌─────────────────────────────┐
        │                             │
        │            📚               │
        │                             │
        │   No books in your library  │
        │                             │
        │   Complete the TODO         │
        │   exercises in              │
        │   BookRepository.kt        │
        │                             │
        └─────────────────────────────┘
```

This is a good UX practice — instead of showing a blank screen, the app:
- Tells the user **what's happening** (no books)
- Guides the user on **what to do** (complete the exercises)
- Uses visual elements (emoji) to make the empty state feel **friendly, not broken**

---

## Summary

The MVVM architecture in this project follows a clean, layered approach:

```
View (BookListScreen) ← observes → ViewModel (BookViewModel)
                                         ↓ calls
                                    UseCase (GetBooksUseCase)
                                         ↓ calls
                                    Repository (BookRepository)
                                         ↓ returns
                                    Model (Book)
```

**Key takeaways:**
- **`data class`** auto-generates `equals`, `hashCode`, `toString`, `copy`
- **`val`** ensures immutability → thread safety + Compose compatibility
- **Repository** abstracts data sources → easy to swap later
- **Use Case** isolates business logic → testable + reusable
- **ViewModel + StateFlow** manages UI state reactively → automatic UI updates
- **LazyColumn** renders efficiently → works for any list size
- **Backing property pattern** (`_books` / `books`) enforces unidirectional data flow
