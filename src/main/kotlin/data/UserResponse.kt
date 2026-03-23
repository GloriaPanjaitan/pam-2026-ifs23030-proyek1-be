package org.delcom.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.delcom.entities.User

@Serializable
data class UserResponse(
    var id: String = "",
    var name: String = "",
    var username: String = "",
    var photo: String? = null,
    var createdAt: Instant? = null,
    var updatedAt: Instant? = null,
) {
    companion object {
        fun fromEntity(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                name = user.name,
                username = user.username,
                photo = user.photo,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
            )
        }
    }
}