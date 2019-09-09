package com.daimler.mbnetworkkit

import com.daimler.mbnetworkkit.header.HeaderService
import com.daimler.mbnetworkkit.header.MemoryHeaderService

object MBNetworkKit {

    private lateinit var headerService: HeaderService

    fun init(config: NetworkServiceConfig) {
        headerService = MemoryHeaderService.fromNetworkServiceConfig(config)
    }

    fun headerService(): HeaderService = headerService
}