@file:OptIn(FlowPreview::class)

package org.hayde117.cmp.book.presentation.book_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hayde117.cmp.book.domain.Book
import org.hayde117.cmp.book.domain.BookRepository
import org.hayde117.cmp.core.domain.onError
import org.hayde117.cmp.core.domain.onSuccess
import org.hayde117.cmp.core.presentation.toUiText

class BookListViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {

    // Cached list of books to avoid repeated repository calls for the same data.
    private var cachedBooks = emptyList<Book>()

    // Jobs to handle concurrent tasks like search and observing favorite books.
    private var searchJob: Job? = null
    private var observeFavoriteJob: Job? = null

    // StateFlow to hold the UI state and allow reactive updates.
    private val _state = MutableStateFlow(BookListState())
    val state = _state
        .onStart {
            // Initialize state with search query observation if no cached books exist.
            if (cachedBooks.isEmpty()) {
                observeSearchQuery()
            }
            // Begin observing favorite books from the repository.
            observeFavoriteBooks()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L), // Share state only while there are active subscribers.
            _state.value // Initial state value.
        )

    /**
     * Handles various user actions such as book clicks, search query changes, and tab selections.
     */
    fun onAction(action: BookListAction) {
        when (action) {
            is BookListAction.OnBookClick -> {
                // Handle book click (e.g., navigate to details screen).
            }

            is BookListAction.OnSearchQueryChange -> {
                // Update the state with the new search query.
                _state.update {
                    it.copy(searchQuery = action.query)
                }
            }

            is BookListAction.OnTabSelected -> {
                // Update the selected tab index in the state.
                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }
        }
    }

    /**
     * Observes favorite books and updates the state whenever the data changes.
     */
    private fun observeFavoriteBooks() {
        observeFavoriteJob?.cancel() // Cancel any ongoing job for observing favorite books.
        observeFavoriteJob = bookRepository
            .getFavoriteBooks() // Retrieve the flow of favorite books.
            .onEach { favoriteBooks ->
                // Update state with the latest favorite books list.
                _state.update {
                    it.copy(favoriteBooks = favoriteBooks)
                }
            }
            .launchIn(viewModelScope) // Launch observation in the ViewModel's scope.
    }

    /**
     * Observes search query changes and triggers appropriate actions like searching or clearing results.
     */
    private fun observeSearchQuery() {
        state
            .map { it.searchQuery } // Extract the search query from the state.
            .distinctUntilChanged() // Skip updates if the query hasn't changed.
            .debounce(500L) // Delay handling to avoid processing too frequently.
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        // Reset to cached books and clear any error message if the query is blank.
                        _state.update {
                            it.copy(
                                errorMessage = null,
                                searchResults = cachedBooks
                            )
                        }
                    }

                    query.length >= 2 -> {
                        // Cancel any ongoing search job and start a new search for valid queries.
                        searchJob?.cancel()
                        searchJob = searchBooks(query)
                    }
                }
            }
            .launchIn(viewModelScope) // Launch observation in the ViewModel's scope.
    }

    /**
     * Searches for books based on the query and updates the state with the results.
     */
    private fun searchBooks(query: String) = viewModelScope.launch {
        _state.update {
            it.copy(isLoading = true) // Indicate loading state.
        }
        bookRepository
            .searchBooks(query) // Perform the search.
            .onSuccess { searchResults ->
                // Update state with successful search results.
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        searchResults = searchResults
                    )
                }
            }
            .onError { error ->
                // Update state with an error message if the search fails.
                _state.update {
                    it.copy(
                        searchResults = emptyList(),
                        isLoading = false,
                        errorMessage = error.toUiText()
                    )
                }
            }
    }
}
