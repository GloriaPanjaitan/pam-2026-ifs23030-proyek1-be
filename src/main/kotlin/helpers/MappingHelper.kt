package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.UserDao
import org.delcom.dao.CategoryDao
import org.delcom.dao.ArticleDao
import org.delcom.dao.CommentDao
import org.delcom.dao.RefreshTokenDao
import org.delcom.entities.User
import org.delcom.entities.Category
import org.delcom.entities.Article
import org.delcom.entities.Comment
import org.delcom.entities.RefreshToken
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun userDaoToModel(dao: UserDao) = User(
    id = dao.id.value.toString(),
    name = dao.name,
    username = dao.username,
    password = dao.password,
    photo = dao.photo,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt,
)

fun categoryDaoToModel(dao: CategoryDao) = Category(
    id = dao.id.value.toString(),
    name = dao.name,
    description = dao.description,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt,
)

fun articleDaoToModel(dao: ArticleDao) = Article(
    id = dao.id.value.toString(),
    title = dao.title,
    content = dao.content,
    thumbnail = dao.thumbnail,
    isPublished = dao.isPublished,
    categoryId = dao.category.id.value.toString(),
    authorId = dao.author.id.value.toString(),
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt,
)

fun commentDaoToModel(dao: CommentDao) = Comment(
    id = dao.id.value.toString(),
    content = dao.content,
    articleId = dao.article.id.value.toString(),
    authorId = dao.author.id.value.toString(),
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt,
)

fun refreshTokenDaoToModel(dao: RefreshTokenDao) = RefreshToken(
    id = dao.id.value.toString(),
    userId = dao.userId.toString(),
    refreshToken = dao.refreshToken,
    authToken = dao.authToken,
    createdAt = dao.createdAt,
)