package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Article

@Serializable
data class ArticleRequest(
    var title: String = "",
    var content: String = "",
    var thumbnail: String? = null,
    var isPublished: Boolean = false,
    var categoryId: String = "",
    var authorId: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "content" to content,
            "thumbnail" to thumbnail,
            "isPublished" to isPublished,
            "categoryId" to categoryId,
            "authorId" to authorId,
        )
    }

    fun toEntity(): Article {
        return Article(
            title = title,
            content = content,
            thumbnail = thumbnail,
            isPublished = isPublished,
            categoryId = categoryId,
            authorId = authorId,
        )
    }
}