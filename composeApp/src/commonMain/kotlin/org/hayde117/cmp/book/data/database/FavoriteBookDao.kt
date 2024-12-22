package org.hayde117.cmp.book.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.hayde117.cmp.book.data.database.BookEntity

@Dao
interface FavoriteBookDao {

    @Upsert
    suspend fun upsert(book: BookEntity)

    @Query("SELECT * FROM book_entity")
    fun getFavoriteBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM book_entity WHERE id = :id")
    suspend fun getFavoriteBook(id: String): BookEntity?

    @Query("DELETE FROM book_entity WHERE id = :id")
    suspend fun deleteFavoriteBook(id: String)
}