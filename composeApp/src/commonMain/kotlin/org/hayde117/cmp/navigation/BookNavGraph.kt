package org.hayde117.cmp.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.hayde117.cmp.book.presentation.SelectedBookViewModel
import org.hayde117.cmp.book.presentation.book_detail.BookDetailAction
import org.hayde117.cmp.book.presentation.book_detail.BookDetailScreenRoot
import org.hayde117.cmp.book.presentation.book_detail.BookDetailViewModel
import org.hayde117.cmp.book.presentation.book_list.BookListScreenRoot
import org.hayde117.cmp.book.presentation.book_list.BookListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation(navController: NavHostController) {

    /** phillip did this but i feel the `BookGraph` is redundant
     * and i would still create a navigation package,
     * extract the nav logic to be inside a nav graph file
     * and just call the nav graph file here for separation of concerns and readability **/

//        NavHost(
//            navController = navController,
//            startDestination = Route.BookGraph
//        ) {
//            navigation<Route.BookGraph>(
//                startDestination = Route.BookList
//            ) {}
    //   }


    NavHost(
        navController = navController,
        startDestination = Route.BookList
    ) {

        composable<Route.BookList>(
            exitTransition = { slideOutHorizontally() },
            popEnterTransition = { slideInHorizontally() }
        ) {
            val viewModel = koinViewModel<BookListViewModel>()
            val selectedBookViewModel =
                it.sharedKoinViewModel<SelectedBookViewModel>(navController)

            // reverts the selected book to null when navigating back from BookDetail screen
            LaunchedEffect(true) {
                selectedBookViewModel.onSelectBook(null)
            }

            BookListScreenRoot(
                viewModel = viewModel,
                onBookClick = { book ->
                    selectedBookViewModel.onSelectBook(book)
                    navController.navigate(
                        Route.BookDetail(book.id)
                    )
                }
            )
        }


        composable<Route.BookDetail>(
            enterTransition = { slideInHorizontally { initialOffset ->
                initialOffset
            } },
            exitTransition = { slideOutHorizontally { initialOffset ->
                initialOffset
            } }
        ) {
            val selectedBookViewModel =
                it.sharedKoinViewModel<SelectedBookViewModel>(navController)
            val viewModel = koinViewModel<BookDetailViewModel>()
            val selectedBook by selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()

            LaunchedEffect(selectedBook) {
                selectedBook?.let {
                    viewModel.onAction(BookDetailAction.OnSelectedBookChange(it))
                }
            }

            BookDetailScreenRoot(
                viewModel = viewModel,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }

}


/** This is used for shared view models hereby
 *  scoping the vm lifecycle to the two screens (nav graph) rather than one screen**/
@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}