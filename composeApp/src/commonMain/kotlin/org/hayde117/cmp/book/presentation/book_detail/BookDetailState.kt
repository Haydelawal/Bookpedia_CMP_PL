package org.hayde117.cmp.book.presentation.book_detail

import org.hayde117.cmp.book.domain.Book

data class BookDetailState(
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val book: Book? = null
)
