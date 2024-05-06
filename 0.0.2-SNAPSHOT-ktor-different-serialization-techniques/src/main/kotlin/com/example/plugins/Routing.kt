package com.example.plugins

import com.example.Glob
import com.github.jdw.seaofshadows.core.generateSession
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun Application.configureRouting() {
    routing {
        // Static plugin. Try to access `/static/index.html`
        staticFiles("/", File("files")) {
            staticResources(basePackage = "", remotePath = "/")
        }

        get(Glob.settings.settingsRestGetEndpoint) {
            val session = generateSession(System.currentTimeMillis())
            call.response.cookies.append(Glob.settings.cookieName, session)
            call.respondText {
                val format = Json {
                    encodeDefaults = true
                    prettyPrint = true
                }
                format.encodeToString(Glob.settings)
            }
        }
    }
}
