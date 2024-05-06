package com.example.plugins

import com.example.Glob
import com.github.jdw.seaofshadows.core.Session
import com.github.jdw.seaofshadows.core.dom.Canvas
import com.github.jdw.seaofshadows.core.messaging.Protocol
import com.github.jdw.seaofshadows.core.messaging.Protocol.SystemTalk.*
import com.github.jdw.seaofshadows.core.messaging.ResultMessage
import com.github.jdw.seaofshadows.core.messaging.SerializationTarget
import com.github.jdw.seaofshadows.webgl1.WebGL1
import com.github.jdw.seaofshadows.webgl1.getContext
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.time.Duration

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket(Glob.settings.endpoints[Protocol.Supported.WEBSOCKET]!!) {
            println("Got connection on ${Glob.settings.endpoints[Protocol.Supported.WEBSOCKET]!!}...")
            val sessionCookie = call.request.cookies[Glob.settings.cookieName]
                ?: close(CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "No session set!"))
            Glob.sessions[sessionCookie as Session] = this

            var serializationTarget: SerializationTarget? = null
            with(incoming.receive() as Frame.Text) {
                println("Received Frame.Text: $this")
                this.readText().let { text ->
                    println("Received text: $text")
                    val value = Protocol.SystemTalk.valueOf(text)
                    when (value) {
                        TALK_PROTOBUF -> serializationTarget = SerializationTarget.PROTOBUF
                        TALK_JSON -> serializationTarget = SerializationTarget.JSON
                        else -> throw Exception("Expected 'TALK_PROTOBUF' or 'TALK_JSON', got '$text'!")
                    }
                }
            }

            with(incoming.receive() as Frame.Text) {
                println("Received Frame.Text: $this")
                this.readText().let { text ->
                    println("Received text: $text")
                    if (Protocol.SystemTalk.CONTEXT_WEBGL1_OK.name != text)
                        throw Exception("Expected 'CONTEXT_WEBGL1_OK', got '$text'!")
                }
            }

            val renderChannel = Channel<String>()
            launch {
                while (!renderChannel.isClosedForSend) {
                    println("Not closed for send")
                    val t = renderChannel.receive()
                    println("Sending t = $t")
                    outgoing.send(Frame.Text(t))
                }
            }

            launch {
                while (true) {
                    with(incoming.receive() as Frame.Text) {
                        println("Received Frame.Text: $this")
                        this.readText().let { text ->
                            try {
                                val rm: ResultMessage = when (serializationTarget!!) {
                                    SerializationTarget.JSON -> Json.decodeFromString(text)
                                    SerializationTarget.PROTOBUF -> ProtoBuf.decodeFromByteArray(text.encodeToByteArray())
                                }

                                println("Succeeded in receiving a ResultMessage!")
                                renderChannel.send(text)
                            }
                            catch (e: Exception) {
                                println("Failed deserializing a ResultMessage!")
                            }
                        }
                    }
                }
            }
            val canvas = Canvas(Glob.settings.expectedCanvasIds.first(), renderChannel, serializationTarget!!)
            val gl = canvas.getContext("webgl")

            gl.clearColor(0.7, 0.2, 0.0, 1.0)
            gl.clear(gl.COLOR_BUFFER_BIT)
        }
    }
}

