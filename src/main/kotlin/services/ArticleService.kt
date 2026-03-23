package org.delcom.services

import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.AppException
import org.delcom.data.ArticleRequest
import org.delcom.data.DataResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IArticleRepository
import java.io.File

class ArticleService(private val articleRepository: IArticleRepository) {

    suspend fun getAll(call: ApplicationCall) {
        // Query params untuk search, filter, dan pagination
        val search     = call.request.queryParameters["search"]?.trim()?.takeIf { it.isNotBlank() }
        val categoryId = call.request.queryParameters["categoryId"]?.trim()?.takeIf { it.isNotBlank() }
        val isPublished = call.request.queryParameters["isPublished"]?.toBooleanStrictOrNull()
        val page       = call.request.queryParameters["page"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val perPage    = call.request.queryParameters["perPage"]?.toIntOrNull()
            ?.coerceIn(1, 100) ?: 10

        val (articles, total) = articleRepository.getAll(
            search = search,
            categoryId = categoryId,
            isPublished = isPublished,
            page = page,
            perPage = perPage,
        )

        val totalPages = if (total == 0L) 1 else ((total + perPage - 1) / perPage).toInt()

        val response = DataResponse(
            status = "success",
            message = "Berhasil mengambil data artikel",
            data = mapOf(
                "articles"   to articles,
                "pagination" to mapOf(
                    "page"       to page,
                    "perPage"    to perPage,
                    "total"      to total,
                    "totalPages" to totalPages,
                    "hasNext"    to (page < totalPages),
                    "hasPrev"    to (page > 1),
                )
            )
        )
        call.respond(response)
    }

    suspend fun getById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        val article = articleRepository.getById(id)
            ?: throw AppException(404, "Artikel tidak ditemukan")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data artikel",
            article
        )
        call.respond(response)
    }

    suspend fun getByCategory(call: ApplicationCall) {
        val categoryId = call.parameters["categoryId"]
            ?: throw AppException(400, "Category Id tidak boleh kosong")

        val articles = articleRepository.getByCategory(categoryId)
        val response = DataResponse(
            "success",
            "Berhasil mengambil data artikel",
            articles
        )
        call.respond(response)
    }

    suspend fun create(call: ApplicationCall) {
        val request = call.receive<ArticleRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("title", "Judul tidak boleh kosong")
        validator.required("content", "Konten tidak boleh kosong")
        validator.required("categoryId", "Kategori tidak boleh kosong")
        validator.validate()

        val authorId = call.principal<JWTPrincipal>()
            ?.payload?.getClaim("userId")?.asString()
            ?: throw AppException(401, "Token tidak valid")

        request.authorId = authorId
        val id = articleRepository.create(request.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil membuat artikel",
            mapOf(Pair("articleId", id))
        )
        call.respond(response)
    }

    suspend fun update(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        articleRepository.getById(id)
            ?: throw AppException(404, "Artikel tidak ditemukan")

        val request = call.receive<ArticleRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("title", "Judul tidak boleh kosong")
        validator.required("content", "Konten tidak boleh kosong")
        validator.required("categoryId", "Kategori tidak boleh kosong")
        validator.validate()

        val updated = articleRepository.update(id, request.toEntity())
        if (!updated) throw AppException(500, "Gagal mengupdate artikel")

        val response = DataResponse(
            "success",
            "Berhasil mengupdate artikel",
            null
        )
        call.respond(response)
    }

    suspend fun delete(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        articleRepository.getById(id)
            ?: throw AppException(404, "Artikel tidak ditemukan")

        val deleted = articleRepository.delete(id)
        if (!deleted) throw AppException(500, "Gagal menghapus artikel")

        val response = DataResponse(
            "success",
            "Berhasil menghapus artikel",
            null
        )
        call.respond(response)
    }

    suspend fun uploadThumbnail(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        articleRepository.getById(id)
            ?: throw AppException(404, "Artikel tidak ditemukan")

        val multipart = call.receiveMultipart()
        val filePathHolder = arrayOfNulls<String>(1)

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val ext = part.originalFileName?.substringAfterLast(".") ?: "jpg"
                val fileName = "${id}.${ext}"
                val file = File("uploads/articles/$fileName")
                file.parentFile?.mkdirs()
                part.streamProvider().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                filePathHolder[0] = "uploads/articles/$fileName"
            }
            part.dispose()
        }

        val filePath = filePathHolder[0]
            ?: throw AppException(400, "Gambar tidak boleh kosong")

        articleRepository.updateThumbnail(id, filePath)

        val response = DataResponse(
            "success",
            "Berhasil mengupload thumbnail",
            mapOf("thumbnail" to filePath)
        )
        call.respond(response)
    }

    suspend fun getThumbnail(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        val article = articleRepository.getById(id)
            ?: throw AppException(404, "Artikel tidak ditemukan")

        val filePath = article.thumbnail
            ?: throw AppException(404, "Thumbnail tidak ditemukan")

        val file = File(filePath)
        if (!file.exists()) throw AppException(404, "File tidak ditemukan")

        call.respondFile(file)
    }
}