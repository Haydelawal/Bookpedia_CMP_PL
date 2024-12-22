package org.hayde117.cmp.book.domain

import kotlinx.coroutines.flow.Flow
import org.hayde117.cmp.core.domain.DataError
import org.hayde117.cmp.core.domain.EmptyResult
import org.hayde117.cmp.core.domain.Result

interface BookRepository {

    suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote>

    suspend fun getBookDescription(bookId: String): Result<String?, DataError>

}

