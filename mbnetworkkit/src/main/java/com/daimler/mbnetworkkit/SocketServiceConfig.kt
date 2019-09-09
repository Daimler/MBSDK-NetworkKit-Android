package com.daimler.mbnetworkkit

import com.daimler.mbnetworkkit.networking.OkhttpSocketConnection
import com.daimler.mbnetworkkit.socket.SocketConnection
import com.daimler.mbnetworkkit.socket.message.MessageProcessor
import com.daimler.mbnetworkkit.socket.reconnect.FixPeriodicReconnection
import com.daimler.mbnetworkkit.socket.reconnect.NoReconnection
import com.daimler.mbnetworkkit.socket.reconnect.Reconnection
import java.util.*
import java.util.concurrent.TimeUnit

class SocketServiceConfig private constructor(
    val sessionId: String,
    internal val socketConnection: SocketConnection
) {

    class Builder(private val url: String, private val messageProcessor: MessageProcessor) {

        private var reconnection: Reconnection = NoReconnection()

        private var sessionId: UUID? = null

        private var networkServiceConfig: NetworkServiceConfig? = null

        /**
         * This will configure the socket to automatically try to reconnect if connection failure occurs.
         * Reconnect will only be executed if cause of lost connection is not an invalid or expired token.
         * If manual connection was initiated while reconnect was in progress, the reconnect will be reset.
         */
        fun tryPeriodicReconnect(periodInSeconds: Long, maxRetries: Int): Builder {
            this.reconnection = FixPeriodicReconnection(TimeUnit.SECONDS.toMillis(periodInSeconds), maxRetries)
            return this
        }

        fun useAppSessionId(appSessionId: UUID): Builder {
            this.sessionId = appSessionId
            return this
        }

        fun useNetworkServiceConfig(networkServiceConfig: NetworkServiceConfig): Builder {
            this.networkServiceConfig = networkServiceConfig
            return this
        }

        fun create(): SocketServiceConfig {
            val sessionId = sessionId?.toString() ?: UUID.randomUUID().toString()
            return SocketServiceConfig(sessionId, createSocketConnection())
        }

        private fun createSocketConnection(): SocketConnection {
            return OkhttpSocketConnection(reconnection, messageProcessor, networkServiceConfig, url)
        }
    }
}