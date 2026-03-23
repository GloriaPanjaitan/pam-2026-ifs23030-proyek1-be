package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.AppException
import org.delcom.data.CategoryRequest
import org.delcom.data.DataResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ICategoryRepository

class CategoryService(private val categoryRepository: ICategoryRepository) {

    suspend fun getAll(call: ApplicationCall) {
        val categories = categoryRepository.getAll()
        val response = DataResponse(
            "success",
            "Berhasil mengambil data kategori",
            categories
        )
        call.respond(response)
    }

    suspend fun getById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        val category = categoryRepository.getById(id)
            ?: throw AppException(404, "Kategori tidak ditemukan")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data kategori",
            category
        )
        call.respond(response)
    }

    suspend fun create(call: ApplicationCall) {
        val request = call.receive<CategoryRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("name", "Nama tidak boleh kosong")
        validator.validate()

        val id = categoryRepository.create(request.toEntity())
        val response = DataResponse(
            "success",
            "Berhasil membuat kategori",
            mapOf(Pair("categoryId", id))
        )
        call.respond(response)
    }

    suspend fun update(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        categoryRepository.getById(id)
            ?: throw AppException(404, "Kategori tidak ditemukan")

        val request = call.receive<CategoryRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("name", "Nama tidak boleh kosong")
        validator.validate()

        val updated = categoryRepository.update(id, request.toEntity())
        if (!updated) throw AppException(500, "Gagal mengupdate kategori")

        val response = DataResponse(
            "success",
            "Berhasil mengupdate kategori",
            null
        )
        call.respond(response)
    }

    suspend fun delete(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "Id tidak boleh kosong")

        categoryRepository.getById(id)
            ?: throw AppException(404, "Kategori tidak ditemukan")

        val deleted = categoryRepository.delete(id)
        if (!deleted) throw AppException(500, "Gagal menghapus kategori")

        val response = DataResponse(
            "success",
            "Berhasil menghapus kategori",
            null
        )
        call.respond(response)
    }
}