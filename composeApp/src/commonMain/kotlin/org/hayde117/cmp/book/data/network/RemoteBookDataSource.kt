package org.hayde117.cmp.book.data.network

import org.hayde117.cmp.core.domain.Result
import org.hayde117.cmp.book.data.dto.SearchResponseDto
import org.hayde117.cmp.core.domain.DataError

interface RemoteBookDataSource {

    suspend fun searchBooks(
        query: String,
        resultLimit: Int? = null
    ): Result<SearchResponseDto, DataError.Remote>
}