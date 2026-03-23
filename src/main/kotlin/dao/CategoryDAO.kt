package org.delcom.dao

import org.delcom.tables.CategoryTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class CategoryDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CategoryDao>(CategoryTable)

    var name by CategoryTable.name
    var description by CategoryTable.description
    var createdAt by CategoryTable.createdAt
    var updatedAt by CategoryTable.updatedAt
}