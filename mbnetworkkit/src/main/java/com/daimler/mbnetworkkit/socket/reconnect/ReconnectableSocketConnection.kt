package com.daimler.mbnetworkkit.socket.reconnect

import com.daimler.mbnetworkkit.socket.ConnectionConfig
import com.daimler.mbnetworkkit.socket.ConnectionError
import com.daimler.mbnetworkkit.socket.SocketConnection
import com.daimler.mbnetworkkit.socket.SocketState
import com.daimler.mbnetworkkit.socket.message.MessageProcessor

/**
 * It provides the possibility to do a reconnect, if the connection was lost. All classes extending
 * this implementation must call super method of the related action.
 */
abstract class ReconnectableSocketConnection(
    private var reconnection: Reconnection,
    messageProcessor: MessageProcessor
) : SocketConnection(messageProcessor), ReconnectListener {

    override fun onStartConnection(config: ConnectionConfig) {
        reconnection.connectingStarted(config)
    }

    override fun onConnectionCompleted() {
        reconnection.reconnectSuccess()
    }

    override fun onStartDisconnect() {
        resetReconnection()
    }

    /**
     * When overriden, the super [ReconnectableSocketConnection.onConnectionError] should be called
     * to return the related [SocketState.ConnectionLost]
     */
    override fun onConnectionError(error: ConnectionError, cause: String): SocketState.ConnectionLost {
        val connectionLostState = SocketState.ConnectionLost(error, cause)
        if (isNetworkError(error)) {
            val reconnect = reconnection.reconnect(this)
            connectionLostState.reconnect = reconnect
        } else {
            resetReconnection()
        }
        return connectionLostState
    }

    override fun onSocketClosed() {
        resetReconnection()
    }

    // region ReconnectListener

    override fun onStartReconnect(connectionConfig: ConnectionConfig) {
        connectToSocket(connectionConfig)
    }

    override fun onReconnectCancelled() {
    }

    // endregion

    private fun isNetworkError(error: ConnectionError): Boolean {
        return error == ConnectionError.NETWORK_FAILURE
    }

    private fun resetReconnection() {
        reconnection.reset()
    }
}