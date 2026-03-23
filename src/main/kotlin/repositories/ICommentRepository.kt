package org.delcom.repositories

import org.delcom.entities.Comment

interface ICommentRepository {
    suspend fun getByArticle(articleId: String): List<Comment>
    suspend fun getById(id: String): Comment?
    suspend fun create(comment: Comment): String
    suspend fun update(id: String, newComment: Comment): Boolean
    suspend fun delete(id: String): Boolean
}