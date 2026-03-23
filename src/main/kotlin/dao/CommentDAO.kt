package org.delcom.dao

import org.delcom.tables.CommentTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class CommentDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CommentDao>(CommentTable)

    var content by CommentTable.content
    var article by ArticleDao referencedOn CommentTable.articleId
    var author by UserDao referencedOn CommentTable.authorId
    var createdAt by CommentTable.createdAt
    var updatedAt by CommentTable.updatedAt
}