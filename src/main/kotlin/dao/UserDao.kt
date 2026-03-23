package org.delcom.dao

import org.delcom.tables.UserTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(UserTable)

    var name by UserTable.name
    var username by UserTable.username
    var password by UserTable.password
    var photo by UserTable.photo
    var createdAt by UserTable.createdAt
    var updatedAt by UserTable.updatedAt
}