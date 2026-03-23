package org.delcom.repositories

import org.delcom.dao.ArticleDao
import org.delcom.dao.CommentDao
import org.delcom.dao.UserDao
import org.delcom.entities.Comment
import org.delcom.helpers.commentDaoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.CommentTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import java.util.UUID

class CommentRepository : ICommentRepository {

    override suspend fun getByArticle(articleId: String): List<Comment> = suspendTransaction {
        CommentDao
            .find { CommentTable.articleId eq UUID.fromString(articleId) }
            .map(::commentDaoToModel)
    }

    override suspend fun getById(id: String): Comment? = suspendTransaction {
        CommentDao
            .find { CommentTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::commentDaoToModel)
            .firstOrNull()
    }

    override suspend fun create(comment: Comment): String = suspendTransaction {
        val commentDao = CommentDao.new {
            content = comment.content
            article = ArticleDao.findById(UUID.fromString(comment.articleId))!!
            author = UserDao.findById(UUID.fromString(comment.authorId))!!
            createdAt = comment.createdAt
            updatedAt = comment.updatedAt
        }
        commentDao.id.value.toString()
    }

    override suspend fun update(id: String, newComment: Comment): Boolean = suspendTransaction {
        val commentDao = CommentDao
            .find { CommentTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (commentDao != null) {
            commentDao.content = newComment.content
            commentDao.updatedAt = newComment.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun delete(id: String): Boolean = suspendTransaction {
        val rowsDeleted = CommentTable.deleteWhere {
            CommentTable.id eq UUID.fromString(id)
        }
        rowsDeleted >= 1
    }
}