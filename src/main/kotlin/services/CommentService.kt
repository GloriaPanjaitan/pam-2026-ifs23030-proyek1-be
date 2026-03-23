package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.AppException
import org.delcom.data.CommentRequest
import org.delcom.data.DataResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ICommentRepository

class CommentService(private val commentRepository: ICommentRepository) {

    suspend fun getByArticle(call: ApplicationCall) {
        val articleId = call.parameters["articleId"]
            ?: throw AppException(400, "Article Id tidak boleh kosong")

        val comments = commentRepository.getByArticle(articleId)
        val response = DataResponse(
            "success",
            "Berhasil mengambil data komentar",
            comments
        )
        call.respond(response)
    }

    suspend fun getById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        val comment = commentRepository.getById(id)
            ?: throw AppException(404, "Komentar tidak ditemukan")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data komentar",
            comment
        )
        call.respond(response)
    }

    suspend fun create(call: ApplicationCall) {
        val request = call.receive<CommentRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("content", "Konten tidak boleh kosong")
        validator.required("articleId", "Article Id tidak boleh kosong")
        validator.validate()

        // ambil authorId dari JWT
        val authorId = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
            ?.payload?.getClaim("userId")?.asString()
            ?: throw AppException(401, "Token tidak valid")

        request.authorId = authorId
        val id = commentRepository.create(request.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil membuat komentar",
            mapOf(Pair("commentId", id))
        )
        call.respond(response)
    }

    suspend fun update(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        commentRepository.getById(id)
            ?: throw AppException(404, "Komentar tidak ditemukan")

        val request = call.receive<CommentRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("content", "Konten tidak boleh kosong")
        validator.validate()

        val updated = commentRepository.update(id, request.toEntity())
        if (!updated) throw AppException(500, "Gagal mengupdate komentar")

        val response = DataResponse(
            "success",
            "Berhasil mengupdate komentar",
            null
        )
        call.respond(response)
    }

    suspend fun delete(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        commentRepository.getById(id)
            ?: throw AppException(404, "Komentar tidak ditemukan")

        val deleted = commentRepository.delete(id)
        if (!deleted) throw AppException(500, "Gagal menghapus komentar")

        val response = DataResponse(
            "success",
            "Berhasil menghapus komentar",
            null
        )
        call.respond(response)
    }
}