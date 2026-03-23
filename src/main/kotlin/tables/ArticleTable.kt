package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ArticleTable : UUIDTable("articles") {
    val title = varchar("title", 200)
    val content = text("content")
    val thumbnail = varchar("thumbnail", 255).nullable()
    val isPublished = bool("is_published").default(false)
    val categoryId = reference("category_id", CategoryTable)
    val authorId = reference("author_id", UserTable)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}