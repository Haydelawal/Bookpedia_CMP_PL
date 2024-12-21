package org.hayde117.cmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.hayde117.cmp.book.presentation.book_list.BookListScreenRoot
import org.hayde117.cmp.book.presentation.book_list.BookListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    BookListScreenRoot(
        viewModel = koinViewModel<BookListViewModel>(),
        onBookClick = {}
    )
}