package org.hayde117.cmp.book.domain

import kotlinx.serialization.SerialName

// using mvi and domain should be isolated in such a way that
// it is just pure kotlin code, don't use stuff like @SerialName
data class Book(
    val id: String,
    val title: String,
    val imageUrl: String,
    val authors: List<String>,
    val description: String?,
    val languages: List<String>,
    val firstPublishYear: String?,
    val averageRating: Double?,
    val ratingCount: Int?,
    val numPages: Int?,
    val numEditions: Int
)
