package com.daimler.mbnetworkkit.header

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class RisHeaderInterceptor(private val headerService: BaseHeaderService) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
                .apply {
                    val headerKeys = request.headers().names()
                    header(HEADER_APPLICATION_NAME, headerService.applicationName)
                    header(HEADER_APPLICATION_VERSION, headerService.applicationVersion)
                    header(HEADER_OS_NAME, headerService.osName)
                    header(HEADER_OS_VERSION, headerService.osVersion)
                    header(HEADER_SDK_VERSION, headerService.sdkVersion)
                    setHeaderIfNotExists(headerKeys, HEADER_LOCALE, headerService.currentNetworkLocale())
                }
                .build()
        return chain.proceed(newRequest)
    }

    private fun Request.Builder.setHeaderIfNotExists(headerKeys: Set<String>, name: String, value: String): Request.Builder {
        if (!headerKeys.contains(name)) {
            header(name, value)
        }
        return this
    }

    private companion object {
        private const val HEADER_APPLICATION_NAME = "X-ApplicationName"
        private const val HEADER_LOCALE = "X-Locale"
        private const val HEADER_APPLICATION_VERSION = "ris-application-version"
        private const val HEADER_OS_VERSION = "ris-os-version"
        private const val HEADER_OS_NAME = "ris-os-name"
        private const val HEADER_SDK_VERSION = "ris-sdk-version"
    }
}