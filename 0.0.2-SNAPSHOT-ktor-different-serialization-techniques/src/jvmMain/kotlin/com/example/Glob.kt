package com.example

import com.github.jdw.seaofshadows.core.Session
import com.github.jdw.seaofshadows.core.Settings
import io.ktor.server.websocket.*
import java.util.concurrent.ConcurrentHashMap

object Glob {
    val settings = Settings()
    val sessions = ConcurrentHashMap<Session, WebSocketServerSession>()
}