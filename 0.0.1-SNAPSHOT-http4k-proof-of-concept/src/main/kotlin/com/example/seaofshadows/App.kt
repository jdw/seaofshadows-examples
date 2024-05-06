package com.example.seaofshadows

import com.github.jdw.seaofshadows.core.MeansOfCommunication
import com.github.jdw.seaofshadows.core.Settings
import com.github.jdw.seaofshadows.core.api.Constants
import com.github.jdw.seaofshadows.core.messages.Render
import com.github.jdw.seaofshadows.webgl1.Payload
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.http4k.websocket.WsStatus


class App {
    companion object {
        val settings = Settings(
            endpoints = mapOf(MeansOfCommunication.WEBSOCKET to "/sea-of-shadows/websocket")
        )

        fun noSessionSet(ws: Websocket) {
            ws.close(WsStatus.REFUSE)
        }

        @OptIn(ExperimentalSerializationApi::class)
        suspend fun waitForAck(ws: Websocket, state: Long): Result<String> {
            var ret: Result<String>? = null

            while (null == ret) {
                ws.onMessage {
                    println("ws.onMessage...")
                    val ack: Render.Ack = ProtoBuf.decodeFromByteArray(it.body.payload.array())
                    println("Received acknowledgement for state ${ack.state}...")

                    ret = if (state != ack.state) {
                        Result.failure(Exception("Expected state 0, got state ${ack.state}"))
                    }
                    else {
                        Result.success("success")
                    }
                }

                delay(100)
            }

            return ret!!
        }
        @OptIn(ExperimentalSerializationApi::class)
        fun render(websocket: Websocket) {
            println("Starting to render...")
            var state = 0L
            var ackedState = -1L

            websocket.onMessage {
                println("ws.onMessage...")
                val ack: Render.Ack = ProtoBuf.decodeFromByteArray(it.body.payload.array())
                println("Received acknowledgement for state ${ack.state}...")

                if (state != ack.state) {
                    throw Exception("Expected state 0, got state ${ack.state}")
                }
                else {
                    ackedState = ack.state
                    println("Expected state $state, got state ${ack.state}")
                }
            }

            websocket
                .also {
                    val payload = ProtoBuf.encodeToByteArray(Payload.ClearColor(0.0F, 0.0F,0.0F, 1F))
                    val message = Render.Message(
                        id = settings.expectedCanvasIds.first(),
                        method = Render.Method.CLEAR_COLOR,
                        state = state,
                        payload = payload)

                    it.send(WsMessage(ProtoBuf.encodeToByteArray(message).inputStream()))
                    println("Sent clearColor message...")
                }
//                .also {
//                    println("acked state=$ackedState, state = $state")
//                    while (state != ackedState) {
//                        val noop = 1
//                        it.onMessage {
//                            println("ws.onMessage...")
//                            val ack: Render.Ack = ProtoBuf.decodeFromByteArray(it.body.payload.array())
//                            println("Received acknowledgement for state ${ack.state}...")
//
//                            if (state != ack.state) {
//                                throw Exception("Expected state 0, got state ${ack.state}")
//                            }
//                            else {
//                                ackedState = ack.state
//                                println("Expected state $state, got state ${ack.state}")
//                            }
//                        }
//                    }
//                }

//                .also {
//                    runBlocking {
//                        val result = async { waitForAck(it, 0) }.await()
//                        result.getOrThrow()
//                    }
//                }
                .also {
                    val payload = ProtoBuf.encodeToByteArray(Payload.Clear(Constants.COLOR_BUFFER_BIT))
                    state = 1
                    val message = Render.Message(
                        id = settings.expectedCanvasIds.first(),
                        method = Render.Method.CLEAR,
                        state = state,
                        payload = payload)

                    it.send(WsMessage(ProtoBuf.encodeToByteArray(message).inputStream()))
                    println("Sent clear message...")
                }
//                .also {
//                    println("acked state=$ackedState, state = $state")
//                    while (state != ackedState) {
//                        val noop = 1
//                    }
//                }
//                .also {
//                    runBlocking {
//                        val result = async { waitForAck(it, 1) }.await()
//                        result.getOrThrow()
//                    }
//                }

            println("Done rendering. Closing socket...")
            websocket.close(WsStatus.NORMAL)
        }
    }
}