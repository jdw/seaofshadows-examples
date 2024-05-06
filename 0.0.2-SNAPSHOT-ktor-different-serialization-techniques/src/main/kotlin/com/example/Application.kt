package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSockets
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class, ExperimentalUnsignedTypes::class)
fun main() {
    //println(Json.encodeToString(ResultMessage(value = "OK", state = 876)))
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSockets()
    configureRouting()
}
