package com.daimler.mbnetworkkit.networking

import android.os.Handler
import android.os.Looper
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbnetworkkit.BuildConfig
import com.daimler.mbnetworkkit.NetworkServiceConfig
import com.daimler.mbnetworkkit.header.MemoryHeaderService
import com.daimler.mbnetworkkit.socket.*
import com.daimler.mbnetworkkit.socket.message.DataSocketMessage
import com.daimler.mbnetworkkit.socket.message.MessageProcessor
import com.daimler.mbnetworkkit.socket.reconnect.ReconnectableSocketConnection
import com.daimler.mbnetworkkit.socket.reconnect.Reconnection
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class OkhttpSocketConnection(reconnection: Reconnection,
                             messageProcessor: MessageProcessor,
                             private val networkServiceConfig: NetworkServiceConfig? = null,
                             private val url: String)
    : ReconnectableSocketConnection(reconnection, messageProcessor) {

    private var handler: Handler? = null

    private var webSocket: WebSocket? = null

    companion object {
        const val DEFAULT_TIMEOUT = 5L
        const val DEFAULT_PING_INTERVAL = 5L
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_APP_SESSION_ID = "APP-SESSION-ID"
        const val HEADER_OUTPUT_FORMAT = "OUTPUT-FORMAT"
        const val SOCKET_CLOSE_STATUS_CODE = 1000
    }

    inner class SocketListenerImpl : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            openConnection()
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            val error: ConnectionError = when {
                response != null && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED -> ConnectionError.UNAUTHORIZED
                response != null && response.code() == HttpURLConnection.HTTP_FORBIDDEN -> ConnectionError.FORBIDDEN
                else -> ConnectionError.NETWORK_FAILURE
            }
            val cause = t?.message ?: "Unknown socket error"
            error(error, cause)
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            processReceivedMessage(DataSocketMessage.StringSocketMessage(System.currentTimeMillis(), text ?: ""))
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            processReceivedMessage(DataSocketMessage.ByteSocketMessage(System.currentTimeMillis(), bytes?.toByteArray() ?: byteArrayOf()))
        }
    }

    private val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .pingInterval(DEFAULT_PING_INTERVAL, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .apply {
                if (this@OkhttpSocketConnection.networkServiceConfig != null) {
                    val interceptor = MemoryHeaderService.fromNetworkServiceConfig(this@OkhttpSocketConnection.networkServiceConfig).createRisHeaderInterceptor()
                    addInterceptor(interceptor)
                }
            }
            .build()

    private fun startConnect(config: ConnectionConfig) {
        val connectionRequest = Request.Builder()
                .url(url)
                .addHeader(HEADER_AUTHORIZATION, config.jwtToken)
                .addHeader(HEADER_APP_SESSION_ID, config.sessionId)
                .addHeader(HEADER_OUTPUT_FORMAT, config.messageType.name)
                .build()
        webSocket = client.newWebSocket(connectionRequest, SocketListenerImpl())
    }

    private fun startDisconnect() {
        webSocket?.close(SOCKET_CLOSE_STATUS_CODE, null)
        webSocket = null
    }

    // region ReconnectableSocketConnection

    override fun onStartConnection(config: ConnectionConfig) {
        MBLoggerKit.d("onStartConnection: session = ${config.sessionId}, messageType = ${config.messageType}")
        super.onStartConnection(config)
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        startConnect(config)
    }

    override fun onConnectionCompleted() {
        MBLoggerKit.d("onConnectionCompleted")
        super.onConnectionCompleted()
    }

    override fun onStartDisconnect() {
        MBLoggerKit.d("onStartDisconnect")
        super.onStartDisconnect()
        startDisconnect()
    }

    override fun onConnectionError(error: ConnectionError, cause: String): SocketState.ConnectionLost {
        MBLoggerKit.e("onConnectionError: ${error.name}, cause: $cause")
        return super.onConnectionError(error, cause)
    }

    override fun onSocketClosed() {
        MBLoggerKit.d("onSocketClosed")
        super.onSocketClosed()
        handler = null
    }

    override fun onStartReconnect(connectionConfig: ConnectionConfig) {
        MBLoggerKit.d("onStartReconnect: session = ${connectionConfig.sessionId}, messageType = ${connectionConfig.messageType}")
        super.onStartReconnect(connectionConfig)
    }

    override fun onReconnectCancelled() {
        MBLoggerKit.d("onReconnectCancelled")
        super.onReconnectCancelled()
    }

    override fun sendMessage(message: DataSocketMessage?): Boolean {
        val sendResult = when (message) {
            null -> {
                MBLoggerKit.i("sendMessage: null was passed")
                false
            }
            is DataSocketMessage.StringSocketMessage -> {
                sendTextOnSocket(message)
            }
            is DataSocketMessage.ByteSocketMessage -> sendBytesOnSocket(message)
        }
        MBLoggerKit.d("sendMessage: ${if (message == null) "null" else message::class.java} -> $sendResult")
        return sendResult
    }

    override fun notifyListenerStateChange(connectionState: ConnectionState, connectionListener: SocketConnectionListener) {
        MBLoggerKit.d("ConnectionState changed -> ${connectionState::class.java.simpleName}")
        postOnMainThread(Runnable {
            connectionListener.connectionStateChanged(connectionState)
        })
    }

    // endregion

    private fun postOnMainThread(runnable: Runnable) {
        handler?.post(runnable)
    }

    private fun sendTextOnSocket(message: DataSocketMessage.StringSocketMessage): Boolean {
        return webSocket?.send(message.content) ?: false
    }

    private fun sendBytesOnSocket(message: DataSocketMessage.ByteSocketMessage): Boolean {
        return webSocket?.send(ByteString.of(message.bytes, 0, message.bytes.size)) ?: false
    }
}