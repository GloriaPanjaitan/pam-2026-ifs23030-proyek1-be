package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object CommentTable : UUIDTable("comments") {
    val content = text("content")
    val articleId = reference("article_id", ArticleTable)
    val authorId = reference("author_id", UserTable)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}