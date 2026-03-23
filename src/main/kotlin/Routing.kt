package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.AuthService
import org.delcom.services.UserService
import org.delcom.services.CategoryService
import org.delcom.services.ArticleService
import org.delcom.services.CommentService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService: AuthService by inject()
    val userService: UserService by inject()
    val categoryService: CategoryService by inject()
    val articleService: ArticleService by inject()
    val commentService: CommentService by inject()

    install(StatusPages) {
        // Tangkap AppException
        exception<AppException> { call, cause ->
            val dataMap: Map<String, List<String>> = parseMessageToMap(cause.message)

            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }

        // Tangkap semua Throwable lainnya
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.fromValue(500),
                message = ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = ""
                )
            )
        }
    }

    routing {
        get("/") {
            call.respondText("Jarvis : Blog Api bos Gloria telah berjalan.")
        }

        // Route Auth
        route("/auth") {
            post("/register") {
                authService.postRegister(call)
            }
            post("/login") {
                authService.postLogin(call)
            }
            post("/refresh-token") {
                authService.postRefreshToken(call)
            }
            post("/logout") {
                authService.postLogout(call)
            }
        }

        authenticate(JWTConstants.NAME) {

            // Route User
            route("/users") {
                get("/me") {
                    userService.getById(call)
                }
                put("/me") {
                    userService.update(call)
                }
                delete("/me") {
                    userService.delete(call)
                }
            }

            // Route Category
            route("/categories") {
                get {
                    categoryService.getAll(call)
                }
                post {
                    categoryService.create(call)
                }
                get("/{id}") {
                    categoryService.getById(call)
                }
                put("/{id}") {
                    categoryService.update(call)
                }
                delete("/{id}") {
                    categoryService.delete(call)
                }
            }

            // Route Article
            route("/articles") {
                get {
                    articleService.getAll(call)
                }
                post {
                    articleService.create(call)
                }
                get("/{id}") {
                    articleService.getById(call)
                }
                get("/category/{categoryId}") {
                    articleService.getByCategory(call)
                }
                put("/{id}") {
                    articleService.update(call)
                }
                delete("/{id}") {
                    articleService.delete(call)
                }
                put("/{id}/thumbnail") {          // ← upload thumbnail
                    articleService.uploadThumbnail(call)
                }
            }

            // Route Images (public, tanpa auth)
            route("/images") {
                get("/articles/{id}") {           // ← ambil thumbnail
                    articleService.getThumbnail(call)
                }
            }


            // Route Comment
            route("/comments") {
                get("/article/{articleId}") {
                    commentService.getByArticle(call)
                }
                post {
                    commentService.create(call)
                }
                get("/{id}") {
                    commentService.getById(call)
                }
                put("/{id}") {
                    commentService.update(call)
                }
                delete("/{id}") {
                    commentService.delete(call)
                }
            }
        }
    }
}