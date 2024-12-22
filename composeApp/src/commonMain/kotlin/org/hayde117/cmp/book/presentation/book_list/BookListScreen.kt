package org.hayde117.cmp.book.presentation.book_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bookpedia_cmp_pl.composeapp.generated.resources.Res
import bookpedia_cmp_pl.composeapp.generated.resources.favorites
import bookpedia_cmp_pl.composeapp.generated.resources.no_favorite_books
import bookpedia_cmp_pl.composeapp.generated.resources.no_search_results
import bookpedia_cmp_pl.composeapp.generated.resources.search_results
import org.hayde117.cmp.book.domain.Book
import org.hayde117.cmp.book.presentation.book_list.components.BookList
import org.hayde117.cmp.book.presentation.book_list.components.BookSearchBar
import org.hayde117.cmp.core.presentation.DarkBlue
import org.hayde117.cmp.core.presentation.DesertWhite
import org.hayde117.cmp.core.presentation.SandYellow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookListScreenRoot(
    viewModel: BookListViewModel = koinViewModel(),
    onBookClick: (Book) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is BookListAction.OnBookClick -> onBookClick(action.book)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun BookListScreen(
    state: BookListState, // The current state of the screen, including search query, tab index, and book lists
    onAction: (BookListAction) -> Unit, // A callback function to handle user actions
) {
    // Access the software keyboard controller for hiding the keyboard programmatically
    val keyboardController = LocalSoftwareKeyboardController.current

    // State for managing horizontal pager and tab index
    val pagerState = rememberPagerState { 2 }
    // State for managing scrolling of the search results list
    val searchResultsListState = rememberLazyListState()
    // State for managing scrolling of the favorite books list
    val favoriteBooksListState = rememberLazyListState()

    // Automatically scrolls to the top of the search results when they are updated
    LaunchedEffect(state.searchResults) {
        searchResultsListState.animateScrollToItem(0)
    }

    // Syncs the pager state with the selected tab index
    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    // Updates the selected tab index based on the current page of the pager
    LaunchedEffect(pagerState.currentPage) {
        onAction(BookListAction.OnTabSelected(pagerState.currentPage))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue) // Sets the background color
            .statusBarsPadding(), // Adds padding to avoid overlapping with the status bar
        horizontalAlignment = Alignment.CenterHorizontally // Aligns child elements horizontally at the center
    ) {
        // Search bar for inputting search queries
        BookSearchBar(
            searchQuery = state.searchQuery, // The current search query
            onSearchQueryChange = {
                onAction(BookListAction.OnSearchQueryChange(it)) // Trigger action when query changes
            },
            onImeSearch = {
                keyboardController?.hide() // Hides the keyboard when the user submits the search
            },
            modifier = Modifier
                .widthIn(max = 400.dp) // Limits the width of the search bar
                .fillMaxWidth() // Ensures the search bar spans the screen width
                .padding(16.dp) // Adds padding around the search bar
        )

        Surface(
            modifier = Modifier
                .weight(1f) // Occupies the remaining vertical space
                .fillMaxWidth(), // Spans the full width of the screen
            color = DesertWhite, // Sets the background color of the surface
            shape = RoundedCornerShape(
                topStart = 32.dp, topEnd = 32.dp // Adds rounded corners to the top of the surface
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally // Centers child elements horizontally
            ) {
                // Tab row for switching between "Search Results" and "Favorites"
                TabRow(
                    selectedTabIndex = state.selectedTabIndex, // The currently selected tab
                    modifier = Modifier
                        .padding(vertical = 12.dp) // Adds vertical padding
                        .widthIn(max = 700.dp) // Limits the width of the tab row
                        .fillMaxWidth(), // Ensures the tab row spans the screen width
                    containerColor = DesertWhite, // Sets the background color of the tab row
                    indicator = { tabPositions -> // Custom indicator for the selected tab
                        TabRowDefaults.SecondaryIndicator(
                            color = SandYellow, // Sets the indicator color
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state.selectedTabIndex]) // Positions the indicator
                        )
                    }
                ) {
                    // Tab for "Search Results"
                    Tab(
                        selected = state.selectedTabIndex == 0, // Determines if the tab is selected
                        onClick = {
                            onAction(BookListAction.OnTabSelected(0)) // Handles tab click action
                        },
                        modifier = Modifier.weight(1f), // Distributes space equally between tabs
                        selectedContentColor = SandYellow, // Color when the tab is selected
                        unselectedContentColor = Color.Black.copy(alpha = 0.5f) // Color when the tab is not selected
                    ) {
                        Text(
                            text = stringResource(Res.string.search_results), // Tab label
                            modifier = Modifier
                                .padding(vertical = 12.dp) // Adds vertical padding around the text
                        )
                    }

                    // Tab for "Favorites"
                    Tab(
                        selected = state.selectedTabIndex == 1,
                        onClick = {
                            onAction(BookListAction.OnTabSelected(1))
                        },
                        modifier = Modifier.weight(1f),
                        selectedContentColor = SandYellow,
                        unselectedContentColor = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = stringResource(Res.string.favorites),
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp)) // Adds spacing between the tab row and the pager

                // Horizontal pager for displaying content of each tab
                HorizontalPager(
                    state = pagerState, // Pager state to control the current page
                    modifier = Modifier
                        .fillMaxWidth() // Ensures the pager spans the screen width
                        .weight(1f) // Occupies the remaining vertical space
                ) { pageIndex ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(), // Ensures the box fills the available space
                        contentAlignment = Alignment.Center // Centers the content inside the box
                    ) {
                        when (pageIndex) {
                            0 -> {
                                // Content for "Search Results"
                                if (state.isLoading) {
                                    CircularProgressIndicator() // Displays a loading indicator
                                } else {
                                    when {
                                        state.errorMessage != null -> {
                                            // Displays an error message if present
                                            Text(
                                                text = state.errorMessage.asString(),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        state.searchResults.isEmpty() -> {
                                            // Displays a message if no search results are found
                                            Text(
                                                text = stringResource(Res.string.no_search_results),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        else -> {
                                            // Displays the list of search results
                                            BookList(
                                                books = state.searchResults,
                                                onBookClick = {
                                                    onAction(BookListAction.OnBookClick(it)) // Handles book click
                                                },
                                                modifier = Modifier.fillMaxSize(),
                                                scrollState = searchResultsListState // Scroll state for the list
                                            )
                                        }
                                    }
                                }
                            }
                            1 -> {
                                // Content for "Favorites"
                                if (state.favoriteBooks.isEmpty()) {
                                    // Displays a message if no favorite books are found
                                    Text(
                                        text = stringResource(Res.string.no_favorite_books),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineSmall,
                                    )
                                } else {
                                    // Displays the list of favorite books
                                    BookList(
                                        books = state.favoriteBooks,
                                        onBookClick = {
                                            onAction(BookListAction.OnBookClick(it))
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                        scrollState = favoriteBooksListState // Scroll state for the list
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
