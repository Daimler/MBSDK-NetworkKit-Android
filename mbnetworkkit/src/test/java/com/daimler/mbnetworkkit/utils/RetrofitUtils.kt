package com.daimler.mbnetworkkit.utils

import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection

internal fun dummyRequest() =
        Request.Builder().url("https://jsonplaceholder.typicode.com").build()

internal fun dummyResponse(request: Request) =
        Response.Builder()
                .apply {
                    request(request)
                    protocol(Protocol.HTTP_1_0)
                    code(HttpURLConnection.HTTP_OK)
                    message("Dummy")
                }.build()