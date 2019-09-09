package com.daimler.mbnetworkkit.header

import okhttp3.Interceptor
import okhttp3.Response

internal class RisHeaderInterceptor(private val headerService: BaseHeaderService) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
                .apply {
                    addHeader(HEADER_APPLICATION_NAME, headerService.applicationName)
                    addHeader(HEADER_APPLICATION_VERSION, headerService.applicationVersion)
                    addHeader(HEADER_OS_NAME, headerService.osName)
                    addHeader(HEADER_OS_VERSION, headerService.osVersion)
                    addHeader(HEADER_SDK_VERSION, headerService.sdkVersion)
                    addHeader(HEADER_LOCALE, headerService.currentNetworkLocale())
                }
                .build()
        return chain.proceed(newRequest)
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