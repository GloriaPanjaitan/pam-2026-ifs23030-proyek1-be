package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Category(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var description: String? = null,
    var createdAt: Instant = Clock.System.now(),
    var updatedAt: Instant = Clock.System.now(),
)