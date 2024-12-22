/**
 * so the api design is not that great, (reference 4:05:00 of video)
 * so these files were created as a work around for just description only
 * again the api design is not that great that's why this is needed
 */

package org.hayde117.cmp.book.data.dto

import kotlinx.serialization.Serializable


@Serializable(with = BookWorkDtoSerializer::class)
data class BookWorkDto(
    val description: String? = null
)
