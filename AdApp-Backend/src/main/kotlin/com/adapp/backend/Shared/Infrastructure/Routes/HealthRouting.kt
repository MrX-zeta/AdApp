package com.adapp.backend.Shared.Infrastructure.Routes

import com.adapp.backend.Shared.Infrastructure.Models.DatabaseHealthResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable

fun Application.configureHealthRouting() {
    routing {
        // GET /health - Verificar estado del servidor
        get("/health") {
            call.respond(
                HttpStatusCode.OK,
                SimpleHealthResponse(
                    status = "UP",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // GET /health/db - Verificar conexión a base de datos
        get("/health/db") {
            val healthResponse = try {
                // Intentar ejecutar una query simple
                transaction {
                    // Ejecutar SELECT 1 para verificar conexión
                    exec("SELECT 1") { }
                }

                DatabaseHealthResponse(
                    status = "CONNECTED",
                    database = "PostgreSQL",
                    message = "Database connection successful"
                )
            } catch (e: Exception) {
                call.application.environment.log.error("Database connection error", e)

                DatabaseHealthResponse(
                    status = "ERROR",
                    database = "PostgreSQL",
                    error = e.message ?: "Unknown error",
                    errorType = e::class.simpleName
                )
            }

            if (healthResponse.status == "CONNECTED") {
                call.respond(HttpStatusCode.OK, healthResponse)
            } else {
                call.respond(HttpStatusCode.ServiceUnavailable, healthResponse)
            }
        }
    }
}

@Serializable
private data class SimpleHealthResponse(
    val status: String,
    val timestamp: Long
)

