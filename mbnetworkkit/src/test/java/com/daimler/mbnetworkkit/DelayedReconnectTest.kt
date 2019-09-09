package com.daimler.mbnetworkkit

import com.daimler.mbnetworkkit.socket.ConnectionConfig
import com.daimler.mbnetworkkit.socket.MessageType
import com.daimler.mbnetworkkit.socket.reconnect.DelayedReconnection
import com.daimler.mbnetworkkit.socket.reconnect.ReconnectListener
import org.junit.Before
import org.junit.Test
import java.util.*

class DelayedReconnectTest {

    var reconnectListenerOnReconnect = false

    var reconnectListenterOnCancelled = false

    @Before
    fun setup() {
        reconnectListenerOnReconnect = false
        reconnectListenterOnCancelled = false
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidRetryAttempts() {
        object : DelayedReconnection(-1) {
            override fun retryDelayInMillis(numberOfRetry: Int): Long {
                return 0
            }
        }
    }

    @Test
    fun validRetryAttepmpts() {
        val numberOfRetries = 1
        val reconnection = object : DelayedReconnection(numberOfRetries) {
            override fun retryDelayInMillis(numberOfRetry: Int): Long {
                return 0
            }
        }
        assert(numberOfRetries == reconnection.maxRetries)
    }

    @Test
    fun notReconnectingAfterInit() {
        val delay = 1000L
        val reconnection = testReconnection(delay, 1)
        assert(reconnection.isReconnecting().not())
    }

    @Test
    fun reconnectingAfterStart() {
        val delay = 1000L
        val reconnection = testReconnection(delay, 1)
        reconnection.connectingStarted(testConnectionConfig())
        reconnection.reconnect(reconnectListener)
        assert(reconnection.isReconnecting())
    }

    @Test
    fun reconnectTriggeredAfterDelay() {
        val delay = 500L
        val reconnection = testReconnection(delay, 1)
        reconnection.connectingStarted(testConnectionConfig())
        reconnection.reconnect(reconnectListener)
        Thread.sleep(700)
        assert(reconnectListenerOnReconnect)
    }

    @Test
    fun reconnectCancelleTriggeredAfterMaxAttemptsReached() {
        val delay = 1000L
        val reconnection = testReconnection(delay, 0)
        reconnection.connectingStarted(testConnectionConfig())
        reconnection.reconnect(reconnectListener)
        assert(reconnectListenterOnCancelled)
    }

    @Test
    fun reconnectCancelleTriggeredBecauseNoConnectionConfig() {
        val delay = 1000L
        val reconnection = testReconnection(delay, 1)
        reconnection.reconnect(reconnectListener)
        assert(reconnectListenterOnCancelled)
    }

    @Test(expected = IllegalArgumentException::class)
    fun reconnectFailedInvalidDelay() {
        val delay = -1L
        val reconnection = testReconnection(delay, 1)
        reconnection.connectingStarted(testConnectionConfig())
        reconnection.reconnect(reconnectListener)
    }

    @Test
    fun reconnectResultInProgressAfterStarted() {
        val delay = 1000L
        val reconnection = testReconnection(delay, 1)
        reconnection.connectingStarted(testConnectionConfig())
        val reconnect = reconnection.reconnect(reconnectListener)
        assert(reconnect.inProgress)
    }

    @Test
    fun reconnectTryIncreasedAfterStarted() {
        val delay = 1000L
        val reconnection = testReconnection(delay, 1)
        reconnection.connectingStarted(testConnectionConfig())
        val reconnect = reconnection.reconnect(reconnectListener)
        assert(reconnect.attempt == 1)
    }

    private fun testReconnection(delayMillis: Long, retries: Int): DelayedReconnection {
        return object : DelayedReconnection(retries) {
            override fun retryDelayInMillis(numberOfRetry: Int): Long {
                return delayMillis
            }
        }
    }

    private fun testConnectionConfig(): ConnectionConfig {
        return ConnectionConfig("", UUID.randomUUID().toString(), MessageType.PROTO)
    }

    private val reconnectListener: ReconnectListener = object : ReconnectListener {
        override fun onStartReconnect(connectionConfig: ConnectionConfig) {
            reconnectListenerOnReconnect = true
        }

        override fun onReconnectCancelled() {
            reconnectListenterOnCancelled = true
        }
    }
}