package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.AppException
import org.delcom.data.AuthRequest
import org.delcom.data.DataResponse
import org.delcom.data.UserResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.helpers.hashPassword
import org.delcom.helpers.verifyPassword
import org.delcom.repositories.IUserRepository

class UserService(private val userRepository: IUserRepository) {

    suspend fun getById(call: ApplicationCall) {
        // ambil userId dari JWT
        val userId = call.principal<JWTPrincipal>()
            ?.payload?.getClaim("userId")?.asString()
            ?: throw AppException(401, "Token tidak valid")

        val user = userRepository.getById(userId)
            ?: throw AppException(404, "User tidak ditemukan")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data user",
            UserResponse.fromEntity(user)
        )
        call.respond(response)
    }

    suspend fun update(call: ApplicationCall) {
        // ambil userId dari JWT
        val userId = call.principal<JWTPrincipal>()
            ?.payload?.getClaim("userId")?.asString()
            ?: throw AppException(401, "Token tidak valid")

        val existing = userRepository.getById(userId)
            ?: throw AppException(404, "User tidak ditemukan")

        val request = call.receive<AuthRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("name", "Nama tidak boleh kosong")
        validator.required("username", "Username tidak boleh kosong")
        validator.required("password", "Password tidak boleh kosong")
        validator.validate()

        if (!verifyPassword(request.password, existing.password))
            throw AppException(400, "Password tidak valid")

        val updatedUser = existing.copy(
            name = request.name,
            username = request.username,
            password = if (request.newPassword.isNotEmpty())
                hashPassword(request.newPassword)
            else existing.password,
        )

        userRepository.update(userId, updatedUser)

        val response = DataResponse(
            "success",
            "Berhasil mengupdate user",
            UserResponse.fromEntity(updatedUser)
        )
        call.respond(response)
    }

    suspend fun delete(call: ApplicationCall) {
        // ambil userId dari JWT
        val userId = call.principal<JWTPrincipal>()
            ?.payload?.getClaim("userId")?.asString()
            ?: throw AppException(401, "Token tidak valid")

        userRepository.getById(userId)
            ?: throw AppException(404, "User tidak ditemukan")

        val deleted = userRepository.delete(userId)
        if (!deleted) throw AppException(500, "Gagal menghapus user")

        val response = DataResponse(
            "success",
            "Berhasil menghapus user",
            null
        )
        call.respond(response)
    }
}