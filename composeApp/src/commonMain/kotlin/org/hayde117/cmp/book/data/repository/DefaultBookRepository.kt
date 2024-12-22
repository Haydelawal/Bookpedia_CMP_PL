package org.hayde117.cmp.book.data.repository

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.hayde117.cmp.book.data.database.FavoriteBookDao
import org.hayde117.cmp.book.data.mappers.toBook
import org.hayde117.cmp.book.data.mappers.toBookEntity
import org.hayde117.cmp.book.data.network.RemoteBookDataSource
import org.hayde117.cmp.book.domain.Book
import org.hayde117.cmp.book.domain.BookRepository
import org.hayde117.cmp.core.domain.DataError
import org.hayde117.cmp.core.domain.EmptyResult
import org.hayde117.cmp.core.domain.Result
import org.hayde117.cmp.core.domain.map

/**
 * DefaultBookRepository is the implementation of the BookRepository interface.
 * It handles operations related to books using both a remote data source and a local database (FavoriteBookDao).
 *
 * @property remoteBookDataSource The data source for fetching book data from a remote server.
 * @property favoriteBookDao The local DAO for managing favorite books in the database.
 */
class DefaultBookRepository(
    private val remoteBookDataSource: RemoteBookDataSource,
    private val favoriteBookDao: FavoriteBookDao
): BookRepository {

    /**
     * Searches for books matching the query string using the remote data source.
     * Maps the results from DTOs to domain models.
     *
     * @param query The search term to look for.
     * @return A Result wrapping a list of books or a DataError.Remote in case of failure.
     */
    override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote> {
        return remoteBookDataSource
            .searchBooks(query)
            .map { dto ->
                dto.results.map { it.toBook() }
            }
    }

    /**
     * Retrieves the description of a book either from the local database or the remote data source.
     * If the book is not found locally, fetches the details remotely.
     *
     * @param bookId The unique ID of the book.
     * @return A Result containing the book description or a DataError.
     */
    override suspend fun getBookDescription(bookId: String): Result<String?, DataError> {
        val localResult = favoriteBookDao.getFavoriteBook(bookId)

        return if (localResult == null) {
            remoteBookDataSource
                .getBookDetails(bookId)
                .map { it.description }
        } else {
            Result.Success(localResult.description)
        }
    }

    /**
     * Fetches a list of favorite books from the local database as a Flow.
     * Maps the database entities to domain models.
     *
     * @return A Flow emitting a list of favorite books.
     */
    override fun getFavoriteBooks(): Flow<List<Book>> {
        return favoriteBookDao
            .getFavoriteBooks()
            .map { bookEntities ->
                bookEntities.map { it.toBook() }
            }
    }

    /**
     * Checks if a specific book is marked as a favorite by verifying its presence in the favorite books list.
     *
     * @param id The ID of the book to check.
     * @return A Flow emitting a Boolean indicating whether the book is a favorite.
     */
    override fun isBookFavorite(id: String): Flow<Boolean> {
        return favoriteBookDao
            .getFavoriteBooks()
            .map { bookEntities ->
                bookEntities.any { it.id == id }
            }
    }

    /**
     * Marks a book as a favorite by inserting or updating its record in the local database.
     * Catches SQLite exceptions and returns appropriate errors if disk is full.
     *
     * @param book The book to mark as favorite.
     * @return An EmptyResult indicating success or a DataError.Local in case of failure.
     */
    override suspend fun markAsFavorite(book: Book): EmptyResult<DataError.Local> {
        return try {
            favoriteBookDao.upsert(book.toBookEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    /**
     * Deletes a book from the favorites list using its ID.
     *
     * @param id The ID of the book to delete from favorites.
     */
    override suspend fun deleteFromFavorites(id: String) {
        favoriteBookDao.deleteFavoriteBook(id)
    }
}
