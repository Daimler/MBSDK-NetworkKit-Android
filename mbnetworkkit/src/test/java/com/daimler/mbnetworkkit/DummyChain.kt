package com.daimler.mbnetworkkit

import com.daimler.mbnetworkkit.utils.dummyRequest
import com.daimler.mbnetworkkit.utils.dummyResponse
import okhttp3.*
import java.util.concurrent.TimeUnit

class DummyChain : Interceptor.Chain {

    private var request: Request = dummyRequest()

    override fun writeTimeoutMillis(): Int = 0

    override fun call(): Call = DummyCall()

    override fun proceed(request: Request): Response {
        this.request = request
        return dummyResponse(request)
    }

    override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun connectTimeoutMillis(): Int = 0

    override fun connection(): Connection? = null

    override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun request(): Request = request

    override fun readTimeoutMillis(): Int = 0
}