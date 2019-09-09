package com.daimler.mbnetworkkit.networking

class RetrofitTask<C> : BaseRetrofitTask<C, C>() {

    override fun onHandleResponseBody(body: C?, responseCode: Int) {
        body?.let {
            complete(it)
        } ?: failEmptyBody(responseCode)
    }
}