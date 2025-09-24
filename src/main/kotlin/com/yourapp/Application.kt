package com.yourapp

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.time.Duration

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    val log = LoggerFactory.getLogger("Application")
    val jdbcUrl = System.getenv("JDBC_DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/romantic_app"
    val dbUser = System.getenv("DB_USER") ?: "jafran"
    val dbPass = System.getenv("DB_PASS") ?: "yourpassword"

    Database.connect(url = jdbcUrl, driver = "org.postgresql.Driver", user = dbUser, password = dbPass)

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
    }

    routing {
        get("/") {
            call.respondText("Ktor + WebSocket is running")
        }

        webSocket("/chat") {
            send("Connected to romantic chat")
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    send("Echo: $text")
                }
            }
        }

        get("/notifications") {
            call.respondText("SSE is not available. This endpoint is a placeholder.")
        }
    }

    log.info("Application module loaded")
}
