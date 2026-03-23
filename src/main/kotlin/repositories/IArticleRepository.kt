package org.delcom.repositories

import org.delcom.entities.Article

interface IArticleRepository {
    suspend fun getAll(
        search: String? = null,
        categoryId: String? = null,
        isPublished: Boolean? = null,
        page: Int = 1,
        perPage: Int = 10,
    ): Pair<List<Article>, Long>

    suspend fun getById(id: String): Article?
    suspend fun getByCategory(categoryId: String): List<Article>
    suspend fun create(article: Article): String
    suspend fun update(id: String, newArticle: Article): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun updateThumbnail(id: String, thumbnail: String): Boolean
}