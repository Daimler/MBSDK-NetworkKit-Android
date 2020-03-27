package com.daimler.mbnetworkkit.socket.message

import java.util.*

sealed class DataSocketMessage : SocketMessage {

    data class ByteSocketMessage(
        override val timestamp: Long,
        val bytes: ByteArray
    ) : DataSocketMessage() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ByteSocketMessage

            if (!Arrays.equals(bytes, other.bytes)) return false
            if (timestamp != other.timestamp) return false

            return true
        }

        override fun hashCode(): Int {
            var result = Arrays.hashCode(bytes)
            result = 31 * result + timestamp.hashCode()
            return result
        }

        override fun toString(): String {
            return "${bytes.size} Bytes"
        }
    }

    data class StringSocketMessage(
        override val timestamp: Long,
        val content: String
    ) : DataSocketMessage() {

        override fun toString(): String {
            return content
        }
    }
}