package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Comment(
    var id: String = UUID.randomUUID().toString(),
    var content: String = "",
    var articleId: String = "",
    var authorId: String = "",
    var createdAt: Instant = Clock.System.now(),
    var updatedAt: Instant = Clock.System.now(),
)