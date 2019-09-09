package com.daimler.mbnetworkkit.header

import okhttp3.Interceptor

internal abstract class BaseHeaderService(
    internal val applicationName: String,
    internal val applicationVersion: String,
    internal val sdkVersion: String,
    internal val osName: String,
    internal val osVersion: String
) : HeaderService {

    override fun createRisHeaderInterceptor(): Interceptor = RisHeaderInterceptor(this)
}