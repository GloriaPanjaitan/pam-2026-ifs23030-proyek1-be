package org.delcom.repositories

import org.delcom.entities.Category

interface ICategoryRepository {
    suspend fun getAll(): List<Category>
    suspend fun getById(id: String): Category?
    suspend fun create(category: Category): String
    suspend fun update(id: String, newCategory: Category): Boolean
    suspend fun delete(id: String): Boolean
}