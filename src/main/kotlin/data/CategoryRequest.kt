package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Category

@Serializable
data class CategoryRequest(
    var name: String = "",
    var description: String? = null,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "description" to description,
        )
    }

    fun toEntity(): Category {
        return Category(
            name = name,
            description = description,
        )
    }
}