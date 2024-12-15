package org.hayde117.cmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.hayde117.cmp.book.presentation.book_list.BookListScreenRoot
import org.hayde117.cmp.book.presentation.book_list.BookListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    BookListScreenRoot(
        viewModel = remember { BookListViewModel() },
        onBookClick = {}
    )
}