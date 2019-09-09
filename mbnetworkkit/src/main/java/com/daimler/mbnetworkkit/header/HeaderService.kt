package com.daimler.mbnetworkkit.header

import okhttp3.Interceptor

interface HeaderService {

    fun createRisHeaderInterceptor(): Interceptor

    fun updateNetworkLocale(locale: String)

    fun currentNetworkLocale(): String
}