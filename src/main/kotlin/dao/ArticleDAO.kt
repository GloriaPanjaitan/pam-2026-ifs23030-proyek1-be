package org.delcom.dao

import org.delcom.tables.ArticleTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ArticleDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ArticleDao>(ArticleTable)

    var title by ArticleTable.title
    var content by ArticleTable.content
    var thumbnail by ArticleTable.thumbnail
    var isPublished by ArticleTable.isPublished
    var category by CategoryDao referencedOn ArticleTable.categoryId
    var author by UserDao referencedOn ArticleTable.authorId
    var createdAt by ArticleTable.createdAt
    var updatedAt by ArticleTable.updatedAt
}