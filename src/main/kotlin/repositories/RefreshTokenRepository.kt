package org.delcom.repositories

import org.delcom.dao.RefreshTokenDao
import org.delcom.entities.RefreshToken
import org.delcom.helpers.refreshTokenDaoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.RefreshTokenTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.util.UUID

class RefreshTokenRepository : IRefreshTokenRepository {

    override suspend fun getByToken(refreshToken: String, authToken: String): RefreshToken? = suspendTransaction {
        RefreshTokenDao
            .find {
                (RefreshTokenTable.refreshToken eq refreshToken) and
                        (RefreshTokenTable.authToken eq authToken)
            }
            .limit(1)
            .map(::refreshTokenDaoToModel)
            .firstOrNull()
    }

    override suspend fun create(newRefreshToken: RefreshToken): String = suspendTransaction {
        val refreshTokenDao = RefreshTokenDao.new {
            userId = UUID.fromString(newRefreshToken.userId)
            refreshToken = newRefreshToken.refreshToken
            authToken = newRefreshToken.authToken
            createdAt = newRefreshToken.createdAt
        }
        refreshTokenDao.id.value.toString()
    }

    override suspend fun delete(authToken: String): Boolean = suspendTransaction {
        val rowsDeleted = RefreshTokenTable.deleteWhere {
            RefreshTokenTable.authToken eq authToken
        }
        rowsDeleted >= 1
    }

    override suspend fun deleteByUserId(userId: String): Boolean = suspendTransaction {
        val rowsDeleted = RefreshTokenTable.deleteWhere {
            RefreshTokenTable.userId eq UUID.fromString(userId)
        }
        rowsDeleted >= 1
    }
}