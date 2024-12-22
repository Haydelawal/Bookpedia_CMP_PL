package org.hayde117.cmp.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    // removed because it is redundant
    @Serializable
    data object BookGraph: Route

    @Serializable
    data object BookList: Route

    @Serializable
    data class BookDetail(val id: String): Route
}