package com.example

import com.example.seaofshadows.App
import com.github.jdw.seaofshadows.core.MeansOfCommunication
import com.github.jdw.seaofshadows.core.generateSession
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.routing.*
import org.http4k.routing.ws.bind
import org.http4k.server.PolyHandler
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.websocket.WsResponse

fun handler(): PolyHandler {
    val static = ResourceLoader.Classpath("static")
    val httpHandler: HttpHandler = routes(
        static(static,
            "html" to ContentType.TEXT_HTML,
            "json" to ContentType.APPLICATION_JSON),

        "/ping" bind GET to {
            Response(OK).body("pong")
        },

        App.settings.settingsRestGetEndpoint bind GET to { req ->
            println("Visit to ${req.uri}")
            val cookie: Cookie = req.cookie(App.settings.cookieName)
                ?: Cookie(name = App.settings.cookieName, value = generateSession(System.currentTimeMillis()))

            val format = Json {
                encodeDefaults = true
                prettyPrint = true
            }
            val body = Body(format.encodeToString(App.settings))

            Response(OK).cookie(cookie).body(body)
        }
    )


    val websocketsHandler = websockets(App.settings.endpoints.get(MeansOfCommunication.WEBSOCKET)!! bind { req: Request ->
        println("Visit to ${App.settings.endpoints.get(MeansOfCommunication.WEBSOCKET)}...")
        val cookie = req.cookie(App.settings.cookieName)
            ?: return@bind WsResponse(App::noSessionSet)

        WsResponse(App::render)
    })


    return PolyHandler(httpHandler, websocketsHandler)
}

fun main() = runBlocking {
    val server = handler().asServer(Undertow(9000)).start()

    println("Server started on port ${server.port()}...")
}
