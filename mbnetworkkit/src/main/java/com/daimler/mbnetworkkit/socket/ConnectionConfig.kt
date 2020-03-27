package com.daimler.mbnetworkkit.socket

data class ConnectionConfig(
    val jwtToken: String,
    val sessionId: String,
    val messageType: MessageType
)