@file:OptIn(FlowPreview::class)

package org.hayde117.cmp.book.presentation.book_list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.hayde117.cmp.book.domain.Book

class BookListViewModel(
 ) : ViewModel() {

    private var cachedBooks = emptyList<Book>()
    private var searchJob: Job? = null
    private var observeFavoriteJob: Job? = null

    private val _state = MutableStateFlow(BookListState())
    val state = _state.asStateFlow()


    fun onAction(action: BookListAction){
        when(action){
            is BookListAction.OnBookClick -> {}
            is BookListAction.OnSearchQueryChange -> {
                _state.update {
                   it.copy(searchQuery = action.query)
                }
            }
            is BookListAction.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index )
                }
            }
        }
    }


}