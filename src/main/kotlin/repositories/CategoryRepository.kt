package org.delcom.repositories

import org.delcom.dao.CategoryDao
import org.delcom.entities.Category
import org.delcom.helpers.categoryDaoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.CategoryTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import java.util.UUID

class CategoryRepository : ICategoryRepository {

    override suspend fun getAll(): List<Category> = suspendTransaction {
        CategoryDao.all().map(::categoryDaoToModel)
    }

    override suspend fun getById(id: String): Category? = suspendTransaction {
        CategoryDao
            .find { CategoryTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::categoryDaoToModel)
            .firstOrNull()
    }

    override suspend fun create(category: Category): String = suspendTransaction {
        val categoryDao = CategoryDao.new {
            name = category.name
            description = category.description
            createdAt = category.createdAt
            updatedAt = category.updatedAt
        }
        categoryDao.id.value.toString()
    }

    override suspend fun update(id: String, newCategory: Category): Boolean = suspendTransaction {
        val categoryDao = CategoryDao
            .find { CategoryTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (categoryDao != null) {
            categoryDao.name = newCategory.name
            categoryDao.description = newCategory.description
            categoryDao.updatedAt = newCategory.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun delete(id: String): Boolean = suspendTransaction {
        val rowsDeleted = CategoryTable.deleteWhere {
            CategoryTable.id eq UUID.fromString(id)
        }
        rowsDeleted >= 1
    }
}