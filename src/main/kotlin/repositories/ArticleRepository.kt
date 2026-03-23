package org.delcom.repositories

import kotlinx.datetime.Clock
import org.delcom.dao.ArticleDao
import org.delcom.dao.CategoryDao
import org.delcom.dao.UserDao
import org.delcom.entities.Article
import org.delcom.helpers.articleDaoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.ArticleTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class ArticleRepository : IArticleRepository {

    override suspend fun getAll(
        search: String?,
        categoryId: String?,
        isPublished: Boolean?,
        page: Int,
        perPage: Int,
    ): Pair<List<Article>, Long> = suspendTransaction {

        var condition: Op<Boolean> = Op.TRUE

        // Search: pakai OrOp untuk menggabungkan title OR content
        if (!search.isNullOrBlank()) {
            val keyword = "%${search.lowercase()}%"
            val titleMatch = ArticleTable.title.lowerCase() like keyword
            val contentMatch = ArticleTable.content.lowerCase() like keyword
            condition = condition and OrOp(listOf(titleMatch, contentMatch))
        }

        if (!categoryId.isNullOrBlank()) {
            condition = condition and (ArticleTable.categoryId eq UUID.fromString(categoryId))
        }

        if (isPublished != null) {
            condition = condition and (ArticleTable.isPublished eq isPublished)
        }

        val total = ArticleDao.find(condition).count()

        val offset = ((page - 1) * perPage).toLong()

        // Gunakan limit(count) dan offset(start) secara terpisah (non-deprecated)
        val articles = ArticleDao
            .find(condition)
            .orderBy(ArticleTable.createdAt to SortOrder.DESC)
            .limit(perPage)
            .offset(offset)
            .map(::articleDaoToModel)

        Pair(articles, total)
    }

    override suspend fun getById(id: String): Article? = suspendTransaction {
        ArticleDao
            .find { ArticleTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::articleDaoToModel)
            .firstOrNull()
    }

    override suspend fun getByCategory(categoryId: String): List<Article> = suspendTransaction {
        ArticleDao
            .find { ArticleTable.categoryId eq UUID.fromString(categoryId) }
            .map(::articleDaoToModel)
    }

    override suspend fun create(article: Article): String = suspendTransaction {
        val articleDao = ArticleDao.new {
            title = article.title
            content = article.content
            thumbnail = article.thumbnail
            isPublished = article.isPublished
            category = CategoryDao.findById(UUID.fromString(article.categoryId))!!
            author = UserDao.findById(UUID.fromString(article.authorId))!!
            createdAt = article.createdAt
            updatedAt = article.updatedAt
        }
        articleDao.id.value.toString()
    }

    override suspend fun update(id: String, newArticle: Article): Boolean = suspendTransaction {
        val articleDao = ArticleDao
            .find { ArticleTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (articleDao != null) {
            articleDao.title = newArticle.title
            articleDao.content = newArticle.content
            articleDao.thumbnail = newArticle.thumbnail
            articleDao.isPublished = newArticle.isPublished
            articleDao.category = CategoryDao.findById(UUID.fromString(newArticle.categoryId))!!
            articleDao.updatedAt = newArticle.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun delete(id: String): Boolean = suspendTransaction {
        val rowsDeleted = ArticleTable.deleteWhere {
            ArticleTable.id eq UUID.fromString(id)
        }
        rowsDeleted >= 1
    }

    override suspend fun updateThumbnail(id: String, thumbnail: String): Boolean = suspendTransaction {
        val articleDao = ArticleDao
            .find { ArticleTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (articleDao != null) {
            articleDao.thumbnail = thumbnail
            articleDao.updatedAt = Clock.System.now()
            true
        } else {
            false
        }
    }
}