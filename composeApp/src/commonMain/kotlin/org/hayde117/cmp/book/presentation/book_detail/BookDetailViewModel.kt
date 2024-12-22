package org.hayde117.cmp.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hayde117.cmp.navigation.Route
import org.hayde117.cmp.book.domain.BookRepository
import org.hayde117.cmp.core.domain.onSuccess

class BookDetailViewModel(
    private val bookRepository: BookRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Extracts the book ID from the SavedStateHandle, ensuring it's correctly passed through navigation routes
    private val bookId = savedStateHandle.toRoute<Route.BookDetail>().id

    // Internal state flow that holds the current state of the BookDetail screen
    private val _state = MutableStateFlow(BookDetailState())

    // Public state flow exposed to observers (e.g., UI), with initialization logic
    val state = _state
        .onStart {
            // Fetches the book description when the state flow starts
            fetchBookDescription()
            // Observes the book's favorite status
            observeFavoriteStatus()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L), // Shares the flow while there are active subscribers
            _state.value // Initial state value
        )

    // Handles user actions dispatched from the UI
    fun onAction(action: BookDetailAction) {
        when (action) {
            // Updates the selected book in the state
            is BookDetailAction.OnSelectedBookChange -> {
                _state.update {
                    it.copy(
                        book = action.book
                    )
                }
            }
            // Toggles the book's favorite status
            is BookDetailAction.OnFavoriteClick -> {
                viewModelScope.launch {
                    if (state.value.isFavorite) {
                        // Removes the book from favorites if it is currently marked as favorite
                        bookRepository.deleteFromFavorites(bookId)
                    } else {
                        // Marks the book as favorite if it is not currently marked as favorite
                        state.value.book?.let { book ->
                            bookRepository.markAsFavorite(book)
                        }
                    }
                }
            }
            else -> Unit // No action for other cases
        }
    }

    // Observes the favorite status of the book and updates the state accordingly
    private fun observeFavoriteStatus() {
        bookRepository
            .isBookFavorite(bookId) // Checks if the book is marked as favorite
            .onEach { isFavorite ->
                _state.update {
                    it.copy(
                        isFavorite = isFavorite
                    )
                }
            }
            .launchIn(viewModelScope) // Launches the observation in the ViewModel's scope
    }

    // Fetches the book's description from the repository and updates the state
    private fun fetchBookDescription() {
        viewModelScope.launch {
            bookRepository
                .getBookDescription(bookId) // Retrieves the book description
                .onSuccess { description ->
                    _state.update {
                        it.copy(
                            book = it.book?.copy(
                                description = description // Updates the book's description
                            ),
                            isLoading = false // Sets loading state to false after data is fetched
                        )
                    }
                }
        }
    }
}
