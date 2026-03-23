package org.delcom

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*

import org.delcom.helpers.JWTConstants
import org.delcom.helpers.configureDatabases
import org.delcom.module.appModule
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    val dotenv = dotenv {
        directory = "."
        ignoreIfMissing = true
    }

    dotenv.entries().forEach {
        System.setProperty(it.key, it.value)
    }

    EngineMain.main(args)
}

fun Application.module() {

    val jwtSecret = environment.config.property("ktor.jwt.secret").getString()

    install(Authentication) {
        jwt(JWTConstants.NAME) {
            realm = JWTConstants.REALM

            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(JWTConstants.ISSUER)
                    .withAudience(JWTConstants.AUDIENCE)
                    .build()
            )

            validate { credential ->
                val userId = credential.payload
                    .getClaim("userId")
                    .asString()

                if (!userId.isNullOrBlank())
                    JWTPrincipal(credential.payload)
                else null
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "status" to "error",
                        "message" to "Token tidak valid"
                    )
                )
            }
        }
    }

    install(CORS) {
        anyHost()
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            registerTypeAdapter(java.time.Instant::class.java, object : com.google.gson.TypeAdapter<java.time.Instant>() {
                override fun write(out: com.google.gson.stream.JsonWriter, value: java.time.Instant?) {
                    out.value(value?.toString())
                }
                override fun read(input: com.google.gson.stream.JsonReader): java.time.Instant? {
                    return java.time.Instant.parse(input.nextString())
                }
            })
        }GET https://pam-2026-ifs23030-proyek1-be.ifs23030.space:8080/users/me

        HTTP/1.1 500 Internal Server Error
        Server: nginx/1.28.0
        Date: Mon, 23 Mar 2026 12:00:16 GMT
        Content-Type: application/json
        Content-Length: 297
        Connection: keep-alive

        {
            "status": "error",
            "message": "Failed making field 'java.time.Instant#seconds' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type.\nSee https://github.com/google/gson/blob/main/Troubleshooting.md#reflection-inaccessible",
            "data": ""
        }
        Response file saved.
        > 2026-03-23T190016.500.json

        Response code: 500 (Internal Server Error); Time: 616ms (616 ms); Content length: 287 bytes (287 B)

    }

    install(Koin) {
        modules(appModule(jwtSecret))
    }

    configureDatabases()
    configureRouting()
}
