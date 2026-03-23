package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Comment

@Serializable
data class CommentRequest(
    var content: String = "",
    var articleId: String = "",
    var authorId: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "content" to content,
            "articleId" to articleId,
            "authorId" to authorId,
        )
    }

    fun toEntity(): Comment {
        return Comment(
            content = content,
            articleId = articleId,
            authorId = authorId,
        )
    }
}