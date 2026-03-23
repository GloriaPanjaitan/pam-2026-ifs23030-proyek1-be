package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Article(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var thumbnail: String? = null,
    var isPublished: Boolean = false,
    var categoryId: String = "",
    var authorId: String = "",
    var createdAt: Instant = Clock.System.now(),
    var updatedAt: Instant = Clock.System.now(),
)