package org.hayde117.cmp.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.hayde117.cmp.book.data.database.DatabaseFactory
import org.hayde117.cmp.book.data.database.FavoriteBookDatabase
import org.hayde117.cmp.book.data.network.KtorRemoteBookDataSource
import org.hayde117.cmp.book.data.network.RemoteBookDataSource
import org.hayde117.cmp.book.data.repository.DefaultBookRepository
import org.hayde117.cmp.book.domain.BookRepository
import org.hayde117.cmp.book.presentation.SelectedBookViewModel
import org.hayde117.cmp.book.presentation.book_detail.BookDetailViewModel
import org.hayde117.cmp.book.presentation.book_list.BookListViewModel
import org.hayde117.cmp.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()
    singleOf(::DefaultBookRepository).bind<BookRepository>()


    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<FavoriteBookDatabase>().favoriteBookDao }

    viewModelOf(::BookListViewModel)
    viewModelOf(::BookDetailViewModel)
    viewModelOf(::SelectedBookViewModel)
}