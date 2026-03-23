package org.delcom.repositories

import org.delcom.dao.UserDao
import org.delcom.entities.User
import org.delcom.helpers.suspendTransaction
import org.delcom.helpers.userDaoToModel
import org.delcom.tables.UserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

class UserRepository : IUserRepository {
    override suspend fun getById(userId: String): User? = suspendTransaction {
        UserDao
            .find { (UserTable.id eq UUID.fromString(userId)) }
            .limit(1)
            .map(::userDaoToModel)
            .firstOrNull()
    }

    override suspend fun getByUsername(username: String): User? = suspendTransaction {
        UserDao
            .find { (UserTable.username eq username) }
            .limit(1)
            .map(::userDaoToModel)
            .firstOrNull()
    }

    override suspend fun create(user: User): String = suspendTransaction {
        val userDAO = UserDao.new {
            name = user.name
            username = user.username
            password = user.password
            createdAt = user.createdAt
            updatedAt = user.updatedAt
        }
        userDAO.id.value.toString()
    }

    override suspend fun update(id: String, newUser: User): Boolean = suspendTransaction {
        val userDAO = UserDao
            .find { UserTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (userDAO != null) {
            userDAO.name = newUser.name
            userDAO.username = newUser.username
            userDAO.password = newUser.password
            userDAO.photo = newUser.photo
            userDAO.updatedAt = newUser.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun delete(id: String): Boolean = suspendTransaction {
        val rowsDeleted = UserTable.deleteWhere {
            UserTable.id eq UUID.fromString(id)
        }
        rowsDeleted >= 1
    }
}