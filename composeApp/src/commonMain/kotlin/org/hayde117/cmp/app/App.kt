package org.hayde117.cmp.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import org.hayde117.cmp.book.presentation.SelectedBookViewModel
import org.hayde117.cmp.book.presentation.book_detail.BookDetailAction
import org.hayde117.cmp.book.presentation.book_detail.BookDetailScreenRoot
import org.hayde117.cmp.book.presentation.book_detail.BookDetailViewModel
import org.hayde117.cmp.book.presentation.book_list.BookListScreenRoot
import org.hayde117.cmp.book.presentation.book_list.BookListViewModel
import org.hayde117.cmp.navigation.MainNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        // declaring navController
        val navController = rememberNavController()

        // navigation entry point
        MainNavigation(navController)
    }
}
