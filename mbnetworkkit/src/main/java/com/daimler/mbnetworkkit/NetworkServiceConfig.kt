package com.daimler.mbnetworkkit

import android.os.Build
import java.util.*

class NetworkServiceConfig private constructor(
    val applicationName: String,
    val applicationVersion: String,
    val sdkVersion: String,
    val osName: String,
    val osVersion: String,
    val locale: String
) {

    class Builder(
        private val applicationName: String,
        private val applicationVersion: String,
        private val sdkVersion: String
    ) {

        private var osVersion: String? = null
        private var locale: String? = null

        fun useOSVersion(osVersion: String): Builder {
            this.osVersion = osVersion
            return this
        }

        fun useLocale(locale: String): Builder {
            this.locale = locale
            return this
        }

        fun build(): NetworkServiceConfig =
                NetworkServiceConfig(
                        applicationName,
                        applicationVersion,
                        sdkVersion,
                        OS_NAME,
                        osVersion ?: Build.VERSION.RELEASE,
                        locale ?: defaultLocale()
                )

        private fun defaultLocale(): String {
            val locale = Locale.getDefault()
            return "${locale.language}-${locale.country}"
        }

        private companion object {
            private const val OS_NAME = "android"
        }
    }
}