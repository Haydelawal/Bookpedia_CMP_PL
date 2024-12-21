package org.hayde117.cmp.book.data.repository

import org.hayde117.cmp.book.data.mappers.toBook
import org.hayde117.cmp.book.data.network.RemoteBookDataSource
import org.hayde117.cmp.book.domain.Book
import org.hayde117.cmp.book.domain.BookRepository
import org.hayde117.cmp.core.domain.DataError
import org.hayde117.cmp.core.domain.Result
import org.hayde117.cmp.core.domain.map


class DefaultBookRepository(
    private val remoteBookDataSource: RemoteBookDataSource,
): BookRepository {

    override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote> {
        return remoteBookDataSource
            .searchBooks(query)
            .map { dto ->
                dto.results.map { it.toBook() }
            }
    }
}